package zhuboss.gateway.facade.mq.message;

/**
 * 遥测
 */
public class ReadMessage extends BaseMessage {
    private Object data;

    public ReadMessage(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String getMessageType() {
        return this.getClass().getSimpleName();
    }
}
