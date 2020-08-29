package zhuboss.gateway.tx.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.common.HourStsHour;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTU2DscCloseMessage;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTU2DscDataMessage;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTU2DscRegisterMessage;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.Dsc2DTURegisterAckMessage;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouUpperDataMessage;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouUpperRegisterMessage;
import zhuboss.gateway.util.JavaUtil;

public class BaseUpstreamHandler extends ChannelInboundHandlerAdapter {


	@Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        try {
        	MyChannelGroup.allChannels.add(ctx.channel());

			HourStsHour[] downHours = new HourStsHour[24];
			HourStsHour[] upperHours = new HourStsHour[24];
			for(int i=0;i<24;i++){
				downHours[i] = new HourStsHour();
				upperHours[i] = new HourStsHour();
			}
			ChannelKeys.setAttr(ctx.channel(), ChannelKeys.tcpDownFlowSts,downHours );
			ChannelKeys.setAttr(ctx.channel(), ChannelKeys.tcpUpperFlowSts,upperHours );
        } finally {
            super.channelRegistered(ctx);
        }
    }

}
