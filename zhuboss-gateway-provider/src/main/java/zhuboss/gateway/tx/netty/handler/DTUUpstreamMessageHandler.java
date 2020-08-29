package zhuboss.gateway.tx.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.common.HourStsHour;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.*;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouUpperDataMessage;
import zhuboss.gateway.tx.gateway.smart.provider.shunzhou.message.ShunZhouUpperRegisterMessage;
import zhuboss.gateway.util.JavaUtil;

public class DTUUpstreamMessageHandler extends ChannelInboundHandlerAdapter {
	static final Logger logger = LoggerFactory.getLogger(DTUUpstreamMessageHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof DTU2DscDataMessage){
			DTU2DscDataMessage obj = (DTU2DscDataMessage)msg;
			if(logger.isDebugEnabled()){
				logger.debug("DTU2DscDataMessage:"+obj.getDtuId()+","+obj.getData().length+","+JavaUtil.bytesToHexString(obj.getData()));
			}
			/**
			 * 注意，因为不了解宏电是否将一条modbus报文封在一个数据包里。且
			 * 当前存在一个问题，当CRC16校验码是xy00的时候，00被宏电给丢掉了。所以猜测宏电一条报文就是一个modbus报文!
			 * 
			 */
			ByteBuf byteBuf = ctx.alloc().buffer();
			byteBuf.writeBytes(obj.getData());

			//TODO 区分DLT645 1997 2007 协议的DTU
			// ...
            IResponseDecoder  responseDecoder = ChannelKeys.readAttr(ctx.channel(), ChannelKeys.RESPONSE_DECODER);
			try{
				responseDecoder.readData(byteBuf);
			}catch(Exception e){
				logger.error("DTU2DscDataMessage:"+obj.getDtuId()+","+obj.getData().length+","+JavaUtil.bytesToHexString(obj.getData(),0,obj.getData().length));
				logger.error(e.getMessage(),e);
				// modbus 解析失败，关闭连接
				ctx.channel().close();
			}finally{
				byteBuf.release();
			}
			
		}else if(msg instanceof DTU2DscRegisterMessage){
			String devNo = ((DTU2DscRegisterMessage)msg).getDtuId();
			logger.info("DTU注册: {}", devNo);
			//TODO 校验dtuId是否合法 by DB
			CollectorService collectorService = SpringContextUtils.getBean(CollectorService.class);
			CollectorPO collectorPO = collectorService.getCollectorPO(devNo);
			if(collectorPO == null){
				logger.error("DTU[{}]不存在",devNo);
				//TODO 先不关闭
				ChannelKeys.setAttr(ctx.channel(),ChannelKeys.COLLECTOR_TEXT,devNo+"，DTU未注册");
//				ctx.close();
				return;
			}
			ChannelKeys.registerGatewayId(ctx.channel(), devNo, CollectorTypeEnum.RAW_HONGDIAN,collectorPO.getAppId());
			//回写应答
			Dsc2DTURegisterAckMessage ackMessage = new Dsc2DTURegisterAckMessage(devNo);
			ctx.channel().writeAndFlush(ackMessage);
			
		}else if(msg instanceof ShunZhouUpperRegisterMessage){
			return;
		}else if(msg instanceof ShunZhouUpperDataMessage){
		}else if(msg instanceof DTU2DscCloseMessage){
			logger.debug("DTU断开:" + ((DTU2DscCloseMessage)msg).getDtuId());
			ctx.channel().close();
		}else {
			super.channelRead(ctx, msg);
		}
		
	}

}
