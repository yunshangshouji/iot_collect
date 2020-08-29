package zhuboss.gateway.tx.channel.task;

import io.netty.channel.Channel;
import lombok.Data;
import zhuboss.gateway.tx.gateway.IResponseDecoder;

public abstract class AbstractTask extends DeviceRequestMessage {

    /**
     * 过期时间，用于判断任务失败。默认6秒
     */
    private Long expireTime = 10l;

    /**
     * 每一个任务都应该有个响应处理器
     * @return
     */
    protected abstract IResponseDecoder getResponseDecoder();


    private IResponseDecoder responseDecoder = null;
    public IResponseDecoder getResponseDecoder(Channel channel){
        if(responseDecoder == null){
            try {
                responseDecoder = (IResponseDecoder)this.getResponseDecoder();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            responseDecoder.setChannel(channel);
        }
        return responseDecoder;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
