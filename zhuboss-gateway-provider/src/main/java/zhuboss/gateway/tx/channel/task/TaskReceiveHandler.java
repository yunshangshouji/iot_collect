package zhuboss.gateway.tx.channel.task;

import io.netty.channel.Channel;

import java.io.IOException;

public interface TaskReceiveHandler<T> {

    void handle(Channel channel, T message) throws IOException;

}
