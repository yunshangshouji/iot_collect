package zhuboss.gateway.tx.netty.handler.log;

import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import zhuboss.gateway.common.HourSts;
import zhuboss.gateway.common.HourStsHour;
import zhuboss.gateway.console.websocket.WebSocketSessionFactory;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.util.JavaUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.websocket.Session;

/**
 * 下行通信数据日志
 */
@Slf4j
public class LogDownByteBufEncoder extends MessageToByteEncoder<ByteBuf>{

	boolean hex;

	public LogDownByteBufEncoder(boolean hex){
		this.hex = hex;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
			throws Exception {
		if(msg.readableBytes()<1) return;

		/**
		 * 统计下行流量
		 */
		HourStsHour[] hours = ChannelKeys.readAttr(ctx.channel(),ChannelKeys.tcpDownFlowSts);
		HourSts.add(hours,msg.readableBytes());
		Integer appId = ChannelKeys.readAttr(ctx.channel(),ChannelKeys.APP_ID);
		if(appId != null){
			HourSts.getHourSts(appId).addDownTcpFlow(msg.readableBytes());
		}

		String devNo = ChannelKeys.readAttr(ctx.channel(), ChannelKeys.COLLECTOR_NO);
		if(hex){
			log.info("下发数据<<{}：{}",devNo,JavaUtil.byteBufToHexString(msg.duplicate()));
		}else{
			ByteBuf dup = msg.duplicate();
			byte[] data = new byte[dup.readableBytes()];
			dup.writeBytes(data);
			log.info("下发数据<<{}：{}",devNo,new String(data));
		}
		/**
		 * 支持在线WebSocket查看TCP通信
		 */
		try {
			String dutId = ChannelKeys.readAttr(ctx.channel(), ChannelKeys.COLLECTOR_NO);
			if(dutId !=null) {
				List<Session> sessionList = WebSocketSessionFactory.getInstance().getSessionMap().get(dutId);
				if(sessionList!=null) {
					Iterator<Session> iterator = sessionList.iterator();
					while(iterator.hasNext()) {
						Session session = iterator.next();
						if(session.isOpen()) {
							session.getBasicRemote().sendText(WebSocketSessionFactory.getDateTime() +" Req:"+JavaUtil.byteBufToHexString(msg.duplicate()));
						}else {
							iterator.remove();
						}
					}
				}
			}
		}catch(Exception e) {
			log.warn("Websocket send fail!" + e.getMessage());
		}
		out.writeBytes(msg);
	}

}
