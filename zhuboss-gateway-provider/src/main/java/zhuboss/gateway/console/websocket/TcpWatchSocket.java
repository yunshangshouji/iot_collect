package zhuboss.gateway.console.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.channel.task.DebugTask;
import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.tx.channel.task.MyStack;
import zhuboss.gateway.util.JavaUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;

/**
 * Basic Echo Client Socket
 */
@ServerEndpoint(value = "/echo")
@Component
@Slf4j
public class TcpWatchSocket
{
    private Session session;
    private String collectorId ;
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        log.info("新的连接.");
        session.getBasicRemote().sendText("Hello");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        this.session = null;
        if(this.collectorId !=null) {
            WebSocketSessionFactory.getInstance().removeListerner(collectorId, session);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
        log.info("ws msg:" + message);
        if(message.startsWith("cid:")) {
            this.collectorId = message.substring(4);
            WebSocketSessionFactory.getInstance().addListerner(collectorId, session);
        }else {
            //发送tcp报文
            Iterator<Channel> iterator = MyChannelGroup.allChannels.iterator();
            Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(this.collectorId);
            if(channel == null){
                session.getBasicRemote().sendText(WebSocketSessionFactory.getDateTime()+"Debug采集器不在线");
                return;
            }

            DebugTask debugTask = new DebugTask();
            final String ascii = "ascii:";
            if(message.startsWith(ascii)){
                debugTask.setRquestData(message.substring(ascii.length()).getBytes());
            }else{
                String trimText = JavaUtil.hexStringToTrim(message);
                debugTask.setRquestData(JavaUtil.hexStringToBytes(trimText));
            }
            MyStack<DeviceRequestMessage> taskStack = ChannelKeys.readAttr(channel, ChannelKeys.REQUEST_STACK);
            if(taskStack == null){
                session.getBasicRemote().sendText("Debug任务队列不存在!");
                return;
            }
            taskStack.push(debugTask);
            synchronized (debugTask){
                debugTask.wait(6*1000); // 最大等待时间6秒
            }
            if(debugTask.getResponseData()!=null){
                if(message.startsWith(ascii)){
                    session.getBasicRemote().sendText("Debug应答：" + new String(debugTask.getResponseData()));
                }else{
                    session.getBasicRemote().sendText("Debug应答：" + JavaUtil.bytesToHexString(debugTask.getResponseData()));
                }
            }else{
                session.getBasicRemote().sendText("Debug未响应!");
            }

        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error(error.getMessage(),error);
    }




}