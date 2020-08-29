package zhuboss.gateway.tx.channel;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import zhuboss.gateway.common.HourStsHour;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.tx.channel.task.MyStack;
import zhuboss.gateway.tx.gateway.IResponseDecoder;

import java.util.Date;
import java.util.Map;

@Slf4j
public class ChannelKeys {
	public static final AttributeKey<String> COLLECTOR_NO = AttributeKey.valueOf("COLLECTOR_NO");

	/**
	 * PLC连接token验证
	 */
	public static final AttributeKey<String> PLC_CONNECT_TOKEN = AttributeKey.valueOf("PLC_CONNECT_TOKEN");

	/**
	 * PLC转发通道
	 */
	public static final AttributeKey<Channel> PLC_CHANNEL = AttributeKey.valueOf("PLC_CHANNEL");
    public static final AttributeKey<String> PLC_CONNECT_ERROR = AttributeKey.valueOf("PLC_CONNECT_ERROR");

	public static final AttributeKey<Integer> APP_ID = AttributeKey.valueOf("APP_ID");
	public static final AttributeKey<CollectorTypeEnum> COLLECTOR_TYPE = AttributeKey.valueOf("COLLECTOR_TYPE");

	/**
	 * 上线注册时间
	 */
	public static final AttributeKey<Date> REGISTER_TIME = AttributeKey.valueOf("REGISTER_TIME");

	public static final AttributeKey<String> COLLECTOR_TEXT = AttributeKey.valueOf("COLLECTOR_TEXT");
	
	public static final AttributeKey<Map<String,Map<String,Object>>> COLLECT_RESULTS = AttributeKey.valueOf("COLLECT_RESULTS");

	/**
	 * 表号为key，抄值为map
	 */
	public static final AttributeKey<Map<Integer,Map<String, Object>>> DLT1997 = AttributeKey.valueOf("DLT1997");

	/**
	 * 读表任务队列
	 */
	public static final AttributeKey<MyStack> REQUEST_STACK = AttributeKey.valueOf("REQUEST_STACK");

	/**
	 * 正在执行的任务
	 */
	public static final AttributeKey<DeviceRequestMessage> EXECUTING_TASK = AttributeKey.valueOf("EXECUTING_TASK");

	/**
	 * 网关执行统计
	 */
	public static final AttributeKey<String> RETRIEVE_STS = AttributeKey.valueOf("RETRIEVE_STS");
	/**
	 * 表号与表类型的映射，用于结果解析
	 */
	public static final AttributeKey<Map<Integer,Integer>> MeterIdMeterType = AttributeKey.valueOf("mMeterIdMeterType");

	public static final AttributeKey<IResponseDecoder> RESPONSE_DECODER = AttributeKey.valueOf("RESPONSE_DECODER");

	/**
	 * 上行流量
	 */
	public static final AttributeKey<HourStsHour[]> tcpUpperFlowSts = AttributeKey.valueOf("TCP_UPPER_FLOW_STS");
	/**
	 * 下行流量
	 */
	public static final AttributeKey<HourStsHour[]> tcpDownFlowSts = AttributeKey.valueOf("TCP_DOWN_FLOW_STS");


	public static <T> T readAttr(Channel channel, AttributeKey<T> attr){
		if(channel == null) throw new RuntimeException("channel can not be null");
		Attribute<T> valueAttr = channel.attr(attr);
		return valueAttr.get();
	}
	
	public static <T> void setAttr(Channel channel,AttributeKey<T> attr, T value){
		if(channel == null) throw new RuntimeException("channel can not be null");
		Attribute<T> valueAttr = channel.attr(attr);
		valueAttr.set(value);
	}

	public static void registerGatewayId(Channel channel, String devNo,CollectorTypeEnum collectorType,Integer appId){
		/**
		 * 如果已经存在注册连接，则断开连接
		 */
		Channel existsChannel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
		if(existsChannel != null){
			log.error("{} may have two device,tick down!",devNo);
				existsChannel.close();
		}
		//
		ChannelKeys.setAttr(channel, ChannelKeys.COLLECTOR_NO, devNo);
		ChannelKeys.setAttr(channel, ChannelKeys.APP_ID, appId);
		ChannelKeys.setAttr(channel, ChannelKeys.COLLECTOR_TYPE, collectorType);
		ChannelKeys.setAttr(channel, ChannelKeys.REGISTER_TIME, new Date());
	}
	
}
