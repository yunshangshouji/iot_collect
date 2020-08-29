package zhuboss.gateway.tx.channel.task;

public abstract class DeviceRequestMessage {

    /**
     * 用于任务队列去重
     * @return
     */
    public abstract String getHashAddr();

}
