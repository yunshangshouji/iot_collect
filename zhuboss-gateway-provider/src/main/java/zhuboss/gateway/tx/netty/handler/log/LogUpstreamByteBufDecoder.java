package zhuboss.gateway.tx.netty.handler.log;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import zhuboss.gateway.common.HourSts;
import zhuboss.gateway.common.HourStsHour;
import zhuboss.gateway.console.websocket.WebSocketSessionFactory;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.util.JavaUtil;

import javax.websocket.Session;
import java.util.Iterator;
import java.util.List;

/**
 * 上行数据通信日志
 */
@Slf4j
public class LogUpstreamByteBufDecoder extends ByteToMessageDecoder {

    boolean hex;

    public LogUpstreamByteBufDecoder(boolean hex) {
        this.hex = hex;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //原封不动交给下一个decoder
        out.add(in.copy());
        /**
         * 统计上行流量
         */
        HourStsHour[] hours = ChannelKeys.readAttr(ctx.channel(),ChannelKeys.tcpUpperFlowSts);
        HourSts.add(hours,in.readableBytes());
        Integer appId = ChannelKeys.readAttr(ctx.channel(),ChannelKeys.APP_ID);
        if(appId != null){
            HourSts.getHourSts(appId).addUpperTcpFlow(in.readableBytes());
        }

        //
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);

        String devNo = ChannelKeys.readAttr(ctx.channel(), ChannelKeys.COLLECTOR_NO);
        log.debug("收到数据>>{}：{}",devNo, hex?JavaUtil.bytesToHexString(data):new String(data));

        // 打印在线WebSocket监控
        try {
            List<Session> sessionList = WebSocketSessionFactory.getInstance().getSessionMap().get(devNo);
            if(sessionList!=null && sessionList.size()>0) {
                if(data == null){
                    ByteBuf duplicate = in.duplicate();
                    data = new byte[duplicate.readableBytes()];
                    duplicate.readBytes(data);
                }
                Iterator<Session> iterator = sessionList.iterator();
                while(iterator.hasNext()) {
                    Session session = iterator.next();
                    if(session.isOpen()) {
                        session.getBasicRemote().sendText(WebSocketSessionFactory.getDateTime() +" Res:"+JavaUtil.bytesToHexString(data));
                    }else {
                        iterator.remove();
                    }
                }
            }
        }catch(Exception e) {
            log.warn("Websocket send fail!" + e.getMessage());
        }

    }
}
