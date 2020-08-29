package zhuboss.gateway.tx.gateway.smart.provider.zhuboss;

import io.netty.channel.Channel;

public interface IDispatcher {

    void dispatch(Channel channel, ZhubossDataPackage zhubossDataPackage) throws Exception;

}
