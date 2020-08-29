package zhuboss.gateway.tx.netty.cross;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import zhuboss.gateway.tx.channel.ChannelKeys;

import java.util.Iterator;

public class CrossChannelGroup extends DefaultChannelGroup {

    public static final CrossChannelGroup crossChannels = new CrossChannelGroup("DTU-ALL-CHANNEL", GlobalEventExecutor.INSTANCE);

    public CrossChannelGroup(EventExecutor executor) {
        super(executor);
    }

    public CrossChannelGroup(String name, EventExecutor executor) {
        super(name, executor);
    }

    public CrossChannelGroup(EventExecutor executor, boolean stayClosed) {
        super(executor, stayClosed);
    }

    public CrossChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
        super(name, executor, stayClosed);
    }



    public Channel findChannelByDevNo(String devNo) {
        Iterator<Channel> iterator = crossChannels.iterator();
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

    public Channel findChannelByPlcToken(String token) {
        Iterator<Channel> iterator = crossChannels.iterator();
        Channel channel = null;
        while (iterator.hasNext()) {
            channel = iterator.next();
            String plcConnectToken = ChannelKeys.readAttr(channel, ChannelKeys.PLC_CONNECT_TOKEN);
            if (plcConnectToken == null || !plcConnectToken.equals(token)) {
                continue;
            }
            return channel;
        }
        return null;
    }
}
