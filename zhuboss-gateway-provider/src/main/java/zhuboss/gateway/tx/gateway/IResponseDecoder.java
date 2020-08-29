package zhuboss.gateway.tx.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import zhuboss.gateway.tx.channel.ChannelKeys;

public abstract class IResponseDecoder {


	private Channel channel;

	public abstract void readData(ByteBuf buf) throws Exception;

	public  abstract void reset();

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * 是否进入解析中，解析中不会有心跳包
	 * @return
	 */
	public abstract boolean hasParsing();
}
