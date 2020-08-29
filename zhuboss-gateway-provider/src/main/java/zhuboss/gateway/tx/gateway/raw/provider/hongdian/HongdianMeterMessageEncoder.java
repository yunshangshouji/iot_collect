package zhuboss.gateway.tx.gateway.raw.provider.hongdian;

import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTUDownMeterMessage;
import zhuboss.gateway.util.JavaUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 */
public class HongdianMeterMessageEncoder extends MessageToByteEncoder<DTUDownMeterMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DTUDownMeterMessage msg, ByteBuf byteBuf) throws Exception {

        String dtuId = ChannelKeys.readAttr(ctx.channel(), ChannelKeys.COLLECTOR_NO);

        byteBuf.writeByte(0x7b); //start flag
        byteBuf.writeByte((byte)0x89); //type
        byte[] lenBytes = JavaUtil.int2Bytes(HongdianUpstreamDecoder.UN_BODY_LENGTH + msg.getByteBuffer().readableBytes());
        byteBuf.writeByte(lenBytes[2]); //length
        byteBuf.writeByte(lenBytes[3]); //length
        byteBuf.writeBytes(dtuId.getBytes());
        byteBuf.writeBytes(msg.getByteBuffer());
        byteBuf.writeByte(0x7b); //end flag

        msg.getByteBuffer().release();//释放

    }

}
