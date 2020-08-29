package zhuboss.gateway.facade.mq.message;

/**
 * 仪表离线消息
 */
public class MeterOnlineMessage extends BaseMessage{

    @Override
    public String getMessageType() {
        return this.getClass().getSimpleName();
    }

}
