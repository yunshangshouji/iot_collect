package zhuboss.gateway.facade.mq.message;

/**
 * 网关离线
 */

public class CollectorOfflineMessage extends BaseMessage{


    @Override
    public String getMessageType() {
        return this.getClass().getSimpleName();
    }
}
