package zhuboss.gateway.tx.netty.handler;

import io.netty.channel.Channel;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.HongdianMeterMessageEncoder;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.HongdianRegisterAckMessageEncoder;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.HongdianUpstreamDecoder;
import zhuboss.gateway.tx.gateway.raw.provider.simple.ZhubossRawMessageDecoder;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.ShunZhouDownMessageEncoder;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.ShunZhouUpstreamDecoder;
import zhuboss.gateway.tx.gateway.raw.provider.simple.SimpleRawMessageEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackageMessageEncoder;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDispatcher;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossUpstreamDecoder;
import zhuboss.gateway.tx.meter.modbus.ModbusRequestEncoder;
import zhuboss.gateway.tx.netty.handler.log.LogDownByteBufEncoder;
import zhuboss.gateway.tx.netty.handler.log.LogUpstreamByteBufDecoder;
import zhuboss.gateway.util.JavaUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UpstreamDecoderChooser extends ByteToMessageDecoder {

    Boolean debugTcp;

    public UpstreamDecoderChooser(Boolean debugTcp) {
        this.debugTcp = debugTcp;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        in.markReaderIndex();
        if(in.readableBytes() == 0) return;

        byte firstByte = in.duplicate().readByte();
        if((firstByte&0xff) == 0x7b){ //宏电协议
            ctx.pipeline().addLast("DTUUpstreamMessageHandler",new DTUUpstreamMessageHandler()); //2. dispatch TransPackage & write ByteBuf to server4Www channel
            ctx.pipeline().addLast(ModbusRequestEncoder.class.getName(),new ModbusRequestEncoder());
            ctx.channel().pipeline().addAfter(UpstreamDecoderChooser.class.getName(),HongdianUpstreamDecoder.class.getName(),new HongdianUpstreamDecoder(false));
            ctx.channel().pipeline().addLast("Dsc2DTURegisterAckMessageEncoder",new HongdianRegisterAckMessageEncoder());
            ctx.channel().pipeline().addLast(HongdianMeterMessageEncoder.class.getName(),new HongdianMeterMessageEncoder()); //下发消息编码
            addLogBufHandler(ctx.channel(),true);

        }else if( (firstByte&0xff) == 0xAA){ //顺舟协议
            ctx.channel().pipeline().addAfter(UpstreamDecoderChooser.class.getName(),ShunZhouUpstreamDecoder.class.getName(),new ShunZhouUpstreamDecoder());
            ctx.channel().pipeline().addLast("ShunZhouDownMessageEncoder",new ShunZhouDownMessageEncoder());
            addLogBufHandler(ctx.channel(),false);

        }else if( firstByte == ZhubossPackageType.REGISTER /* 0xBB */){ //ZhuBoss智能协议
            ctx.channel().pipeline().addAfter(UpstreamDecoderChooser.class.getName(),
                    ZhubossUpstreamDecoder.class.getName(),
                    new ZhubossUpstreamDecoder(SpringContextUtils.getBean(ZhubossDispatcher.class))
            );
            ctx.channel().pipeline().addLast(ZhubossDataPackageMessageEncoder.class.getName(),new ZhubossDataPackageMessageEncoder());

        }else if(firstByte ==0x0){ //ZhuBoss透传协议
            in.readByte(); //跳过标志位
            ctx.pipeline().addLast(ModbusRequestEncoder.class.getName(),new ModbusRequestEncoder());
            ctx.channel().pipeline().addAfter(UpstreamDecoderChooser.class.getName(), ZhubossRawMessageDecoder.class.getName(),new ZhubossRawMessageDecoder()); //上传解析
            ctx.channel().pipeline().addLast(SimpleRawMessageEncoder.class.getName(),new SimpleRawMessageEncoder()); //下发消息编码
            addLogBufHandler(ctx.channel(),true);

        }
        /*else if(firstByte == (byte)'A' || firstByte == (byte)'9' ){ //brimesh 上海光因透传设备(没有注册包)
            ctx.pipeline().addLast(ModbusRequestEncoder.class.getName(),new ModbusRequestEncoder());
            ctx.channel().pipeline().addAfter(UpstreamDecoderChooser.class.getName(), SimpleRawMessageDecoder.class.getName(),new SimpleRawMessageDecoder()); //上传解析
            ctx.channel().pipeline().addLast(SimpleRawMessageEncoder.class.getName(),new SimpleRawMessageEncoder()); //下发消息编码
            addLogBufHandler(ctx.channel(),true);

        }*/
       else{
            log.warn("Unsupport protocol header不支持的协议头0x{}", JavaUtil.bytesToHexString(new byte[]{firstByte}));
            ctx.close();
            return;
        }
        ctx.channel().pipeline().remove(this);
        /**
         * 不管哪种协议，都要创建以下变量
         */
        ChannelKeys.setAttr(ctx.channel(),ChannelKeys.DLT1997,new HashMap<Integer, Map<String, Object>>() );
        ChannelKeys.setAttr(ctx.channel(),ChannelKeys.MeterIdMeterType,new HashMap<Integer,Integer>());
        ChannelKeys.setAttr(ctx.channel(), ChannelKeys.COLLECT_RESULTS, new HashMap<>()); //保存采集结果记录

    }

    private void addLogBufHandler(Channel channel,boolean hex){
        if(this.debugTcp){
            channel.pipeline().addFirst(LogUpstreamByteBufDecoder.class.getName(),new LogUpstreamByteBufDecoder(hex));
            channel.pipeline().addLast(LogDownByteBufEncoder.class.getName(),new LogDownByteBufEncoder(hex));
        }
    }
}
