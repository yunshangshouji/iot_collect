package zhuboss.gateway.tx.netty.cross;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackageMessageEncoder;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossUpstreamDecoder;
import zhuboss.gateway.tx.netty.handler.UpstreamDecoderChooser;
import zhuboss.gateway.util.JavaUtil;

import java.util.List;

@Slf4j
public class CrossDecoderChooser extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        in.markReaderIndex();
        if(in.readableBytes() == 0) return;

        byte firstByte = in.duplicate().readByte();
        if( firstByte == ZhubossPackageType.REGISTER /* 0xBB */){ //ZhuBoss智能协议
            ctx.channel().pipeline().addAfter(CrossDecoderChooser.class.getName(), ZhubossUpstreamDecoder.class.getName(),
                    new ZhubossUpstreamDecoder(SpringContextUtils.getBean(CrossDispatcher.class))
            );
            ctx.channel().pipeline().addLast(ZhubossDataPackageMessageEncoder.class.getName(),new ZhubossDataPackageMessageEncoder());

        }else if((firstByte&0xff) ==0xB1){ // PLC 网关端协议
            in.readByte(); //跳过标志位
            ctx.channel().pipeline().addLast(PlcGatewayDispatcher.class.getName(),new PlcGatewayDispatcher());

        }else if((firstByte&0xff) ==0xB2){ // PLC 客户端协议
            in.readByte(); //跳过标志位
            ctx.channel().pipeline().addLast(PlcClientDispatcher.class.getName(),new PlcClientDispatcher());

        }else{
            log.warn("Unsupport protocol header不支持的协议头0x{}", JavaUtil.bytesToHexString(new byte[]{firstByte}));
            ctx.close();
            return;
        }
        ctx.channel().pipeline().remove(this);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        try {
            //注册
            CrossChannelGroup.crossChannels.add(ctx.channel());
            //TODO 加入流量监控
        } finally {
            super.channelRegistered(ctx);
        }
    }


}
