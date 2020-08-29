package zhuboss.gateway.tx.channel;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Iterator;

public class MyChannelGroup extends DefaultChannelGroup {

    public static final MyChannelGroup allChannels = new MyChannelGroup("DTU-ALL-CHANNEL", GlobalEventExecutor.INSTANCE);

    public MyChannelGroup(EventExecutor executor) {
        super(executor);
    }

    public MyChannelGroup(String name, EventExecutor executor) {
        super(name, executor);
    }

    public MyChannelGroup(EventExecutor executor, boolean stayClosed) {
        super(executor, stayClosed);
    }

    public MyChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
        super(name, executor, stayClosed);
    }

    public Channel findChannelByDevNo(String devNo) {
        Iterator<Channel> iterator = allChannels.iterator();
        Channel channel = null;
        while (iterator.hasNext()) {
            channel = iterator.next();
            String channelDevNO = ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_NO);
            if (channelDevNO == null || !channelDevNO.equals(devNo)) {
                continue;
            }
            return channel;
        }
        return null;
    }

}
