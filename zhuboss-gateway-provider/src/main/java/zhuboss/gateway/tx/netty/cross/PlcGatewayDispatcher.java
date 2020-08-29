package zhuboss.gateway.tx.netty.cross;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.netty.cross.CrossChannelGroup;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
public class PlcGatewayDispatcher extends ByteToMessageDecoder {

    enum State{
        register,
        data
    }
    State state = State.register;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int registerSize = 0;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Channel clientChannel = null;
        if(state.equals(State.register)){
            while(registerSize<32 && in.readableBytes()>0){
                baos.write(in.readByte());
                registerSize++;
            }
            if (registerSize == 32){
                //唤醒等待的客户端
                String token = new String(baos.toByteArray());
                clientChannel = CrossChannelGroup.crossChannels.findChannelByPlcToken(token);
                if(clientChannel == null){ //连接已经销毁
                    ctx.close();
                    return;
                }
                ChannelKeys.setAttr(clientChannel,ChannelKeys.PLC_CHANNEL,ctx.channel());
                ChannelKeys.setAttr(ctx.channel(),ChannelKeys.PLC_CHANNEL,clientChannel);
                synchronized (clientChannel){
                    clientChannel.notifyAll();
                }
                state = State.data;
            }else{
                return;
            }
        }

        //转发模式
        clientChannel = ChannelKeys.readAttr(ctx.channel(),ChannelKeys.PLC_CHANNEL);
        if(in.readableBytes() == 0){
            return;
        }
        if(!clientChannel.isActive()){
            ctx.close();
            return;
        }
        /**
         * 零拷贝并没有带来性能的提升
         */
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer(1); //        ByteBuf writeByteBuf = Unpooled.wrappedBuffer(in);
        compositeByteBuf.addComponent(true,in);
        in.retain();
        clientChannel.writeAndFlush(compositeByteBuf);
        in.readerIndex(in.readableBytes());

        /*ByteBuf newWriteBuf = PooledByteBufAllocator.DEFAULT.buffer(in.readableBytes()); //必须初始大小，默认512，溢出将丢弃
        in.readBytes(newWriteBuf);
        clientChannel.writeAndFlush(newWriteBuf).sync();*/

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
        super.exceptionCaught(ctx, cause);
    }
}
