package zhuboss.gateway.tx.channel.task;

import lombok.Data;
import zhuboss.gateway.tx.gateway.IResponseDecoder;
import zhuboss.gateway.tx.meter.DebugResponseDecoder;

/**
 * 用于解决调试人员向网关发送数据包调试分析
 */
@Data
public class DebugTask extends AbstractTask {
    //TODO 像write task 一样，应答后，notify通知锁定线程

    //网关下发
    private byte[] rquestData;

    //网关上报
    private byte[] responseData;

    @Override
    public String getHashAddr() {
        return System.currentTimeMillis()+"" ;
    }

    @Override
    public IResponseDecoder getResponseDecoder() {
        return new DebugResponseDecoder();
    }
}
