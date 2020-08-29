package zhuboss.gateway.adapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.framework.utils.ObjectId;
import zhuboss.gateway.adapter.bean.ModbusMeterType;
import zhuboss.gateway.adapter.bean.ProfileInfo;
import zhuboss.gateway.adapter.bean.ReadInfo;
import zhuboss.gateway.common.DevType;
import zhuboss.gateway.common.OnlineEventType;
import zhuboss.gateway.facade.mq.message.CollectorOfflineMessage;
import zhuboss.gateway.facade.mq.message.MeterOfflineMessage;
import zhuboss.gateway.facade.mq.message.ReadMessage;
import zhuboss.gateway.facade.mq.message.SignalMessage;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.MeterKindService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.spring.mq.MqttSender;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo.ZhubossMeterOffline;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo.ZhubossMeterOnline;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo.ZhubossOnSignal;
import zhuboss.gateway.util.JavaUtil;
import zhuboss.gateway.util.MeterUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component("datacollector-TaskScheduler")
@Slf4j
public class TaskScheduler  implements InitializingBean{
	static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);
	
	ExecutorService fixedThreadPool;
	
	Integer poolSize = 3;
	
	Integer queueCapacity = 1000;//队列最大长度
	@Autowired
	CollectorService  collectorService ;
	@Autowired
    MeterPOMapper meterPOMapper;
	@Autowired
    MeterService meterService;
	@Autowired
	MeterTypeFactory meterTypeFactory;
	@Autowired
	MeterTypeService meterTypeService;
	@Autowired
	MeterKindService meterKindService;
	@Autowired
	MqttSender sender;
	@Autowired
	AlarmOverLimitPOMapper alarmOverLimitPOMapper;
	@Autowired
	LogOverLimitPOMapper logOverLimitPOMapper;
	@Autowired
	LogLostPOMapper logLostPOMapper;
	@Autowired
	LogSignalPOMapper logSignalPOMapper;
	@Autowired
	MeterKindReadPOMapper meterKindReadPOMapper;

	BlockingQueue<Runnable> queue;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		queue = new LinkedBlockingQueue<Runnable>(queueCapacity);
		fixedThreadPool = new ThreadPoolExecutor( //参考Executors.newFixedThreadPool(3)
				1, //keep in the pool, even if they are idle
				poolSize, //the maximum number of threads
				30, //30秒后回收空闲线程
				TimeUnit.SECONDS,
				queue
				
				);
	}
	
	public void addRecord(final String devNo,final  Integer comPort,final Integer loraAddr,String ip,Integer port,final long addr,final Date reportTime,final Date readTime,byte[] data) {
		fixedThreadPool.submit(new Runnable() {
			@Override
            public void run() {
                try {
                    CollectorPO collectorPO = collectorService.getCollectorPO(devNo);
                    if(collectorPO == null){
                        logger.error("采集器不存在{}",devNo);
						return;
					}
					MeterPO meterPO = meterService.select(collectorPO.getId(),comPort,loraAddr,ip,port,addr);
					if(meterPO == null){
						logger.error("表终端不存在{}.{}.{}",devNo,comPort,addr);
						return;
					}

					//TODO 有多个
                    Map<String,Object> values =  new HashMap<>();
					meterTypeFactory.parseRead(meterPO.getMeterTypeId(),null, data,values);
					processPrecision(meterPO.getMeterTypeId(),values);
					processExpression(meterPO.getMeterTypeId(),values);

                    //保存到数据库
					meterService.saveRead(meterPO.getId(),reportTime,readTime,true,JSON.toJSONString(values));
					//保存历史数据
					meterKindService.doPersist(meterPO.getMeterKindId(),meterPO.getId(),readTime,values);

                    if(values.size() !=0){
						sender.sendToMqtt(collectorPO.getAppId(),meterPO.getId(),new ReadMessage(values));
					}else{
						logger.warn("上传空的数据包");
					}

                } catch (Exception e) {
                    logger.error("保存数据失败" + e.getMessage() + ":" + JavaUtil.bytesToHexString(data),e);
                }
            }
		});
	}

	/**
	 * 适用透传网关，智能网关不需要
	 * @param collectorId
	 * @param comPort
	 * @param addr
	 * @param readDate
	 * @param errorMsg
	 */
	public void addReadError(final String collectorId,final Integer comPort,final Integer loraAddr,String ip,Integer port,final long addr,final Date readDate,String errorMsg){
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					CollectorPO collectorPO = collectorService.getCollectorPO(collectorId);
					if(collectorPO == null){
						logger.error("采集器不存在{}",collectorId);
						return;
					}
					MeterPO meterPO = meterService.select(collectorPO.getId(),comPort,loraAddr,ip,port,addr);
					if(meterPO == null){
						logger.error("表终端不存在{}.{}.{}",collectorId,comPort,addr);
						return;
					}
					meterService.saveRead(meterPO.getId(),readDate,readDate,false,errorMsg);
					/**
					 * 发送MQ事件
					 */
					MeterOfflineMessage meterOfflineMessage = new MeterOfflineMessage();
					meterOfflineMessage.setId(ObjectId.get());
					meterOfflineMessage.setCollectorId(collectorPO.getId());
					meterOfflineMessage.setHappenTime(new Date());
					meterOfflineMessage.setMeterId(meterPO.getId());
					sender.sendToMqtt(collectorPO.getAppId(),meterPO.getId(), meterOfflineMessage);
				} catch (Exception e) {
					logger.warn("保存数据失败" + e.getMessage() ,e);
				}
			}
		});
	}

	public void addRecord(final String collectorId,final Integer comPort,final Integer loraAddr,final String ip,final Integer port,final Long addr,final Date reportTime,final Date readTime,Map<String,Object> valueMap,String errMsg) {
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					CollectorPO collectorPO = collectorService.getCollectorPO(collectorId);
					if(collectorPO == null){
						logger.error("采集器不存在{}",collectorId);
						return;
					}
					MeterPO meterPO = meterService.select(collectorPO.getId(),comPort,loraAddr,ip,port,addr);
					if(meterPO == null){
						logger.error("表终端不存在{}.{}.{}",collectorId,comPort,addr);
						return;
					}
					boolean result;
					String content;
					if(valueMap == null || valueMap.size() ==0){
						result = false;
						content = errMsg;
					}else{
						result = true;
						if(meterTypeFactory.load(meterPO.getMeterTypeId()) instanceof ModbusMeterType){
							processPrecision(meterPO.getMeterTypeId(),valueMap);
							processExpression(meterPO.getMeterTypeId(),(Map)valueMap);
						}
						content = JSON.toJSONString(valueMap);
					}
					//保存数据库
					meterService.saveRead(meterPO.getId(),reportTime,readTime,result,content);
					//保存历史数据
					meterKindService.doPersist(meterPO.getMeterKindId(),meterPO.getId(),readTime,valueMap);

					//检查越限解除
					List<AlarmOverLimitPO> txOverLimitPOList = alarmOverLimitPOMapper.selectByClause(new QueryClauseBuilder().andEqual(LogMeter.Fields.METER_ID,meterPO.getId()));
					//check 是否还在告警中
					for(AlarmOverLimitPO txOverLimitPO : txOverLimitPOList){
						BigDecimal readValue = TypeUtils.castToBigDecimal( valueMap.get(txOverLimitPO.getVar()));
						BigDecimal fromValue = txOverLimitPO.getFromValue();
						BigDecimal toValue = txOverLimitPO.getToValue();
						if (
								(fromValue == null || fromValue.compareTo(readValue) <= 0)
										&& (toValue == null || toValue.compareTo(readValue) >= 0)
						) {
							continue;
						}
						//解除告警
						alarmOverLimitPOMapper.deleteByPK(txOverLimitPO.getId());
						//更改报警记录
						LogOverLimitPO logOverLimitPO =  logOverLimitPOMapper.selectByPK(txOverLimitPO.getId());
						logOverLimitPO.setClosed(1);
						logOverLimitPO.setCloseTime(new Date());
						logOverLimitPOMapper.updateByPK(logOverLimitPO);
					}

					//发出MQ
					if(result == true){
						sender.sendToMqtt(collectorPO.getAppId(),meterPO.getId(),new ReadMessage(valueMap));
					}

				} catch (Exception e) {
					logger.warn("保存数据失败" + e.getMessage() ,e);
				}
			}
		});
	}

	public void addAlarm(final String collectorId,final Integer comPort,final Integer loraAddr,final String ip,final Integer port,final Long terId,Map<String,Object> values,MeterAlarmPO meterAlarm,String code,String targetName,BigDecimal value) {
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					CollectorPO collectorPO = collectorService.getCollectorPO(collectorId);
					if(collectorPO == null){
						logger.error("采集器不存在{}",collectorId);
						return;
					}
					MeterPO meterPO = meterService.select(collectorPO.getId(),comPort,loraAddr,ip,port,terId);
					if(meterPO == null){
						logger.error("表终端不存在{}.{}.{}",collectorId,comPort,terId);
						return;
					}
					AlarmOverLimitPO alarmOverLimitPO = new AlarmOverLimitPO();
					alarmOverLimitPO.setId(ObjectId.get());
					alarmOverLimitPO.setAppId(collectorPO.getAppId());
					alarmOverLimitPO.setStationId(collectorPO.getStationId());
					alarmOverLimitPO.setHappenTime(new Date());
					alarmOverLimitPO.setMeterKindId(meterPO.getMeterKindId());
					alarmOverLimitPO.setMeterAlarmId(meterAlarm.getId());
					alarmOverLimitPO.setMeterId(meterPO.getId());
					alarmOverLimitPO.setMeterName(MeterUtil.getMeterName(meterPO));
					alarmOverLimitPO.setMeterId(meterPO.getId());
					alarmOverLimitPO.setMeterKindId(meterPO.getMeterKindId());
					processPrecision(meterPO.getMeterTypeId(),values);
					alarmOverLimitPO.setCreateTime(new Date());
					alarmOverLimitPO.setVar(code);
					alarmOverLimitPO.setVarName(targetName);
					alarmOverLimitPO.setReadValue(value);
					alarmOverLimitPO.setFromValue(meterAlarm.getFromValue());
					alarmOverLimitPO.setToValue(meterAlarm.getToValue());
					alarmOverLimitPO.setTitle(meterAlarm.getTitle());
					//TODO 暂时注释MQTT
//					sender.send(alarmOverLimitPO);
					//保存到数据库
					alarmOverLimitPOMapper.insert(alarmOverLimitPO);
//					alarmOverLimitPOMapper.insert2(txAlarmPO);
				} catch (Exception e) {
					logger.error("保存数据失败" + e.getMessage(),e );
				}
			}
		});
	}

	public void sendSignalMQ(final Integer collectorId,final ZhubossOnSignal zhubossOnSignal) {
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					for(Map.Entry<String,Object> entry : zhubossOnSignal.getValues().entrySet()){
						String targetCode = entry.getKey();
						BigDecimal val = TypeUtils.castToBigDecimal(entry.getValue());
						//
						MeterPO meterPO  = meterService.select(collectorId,zhubossOnSignal.getCom(),zhubossOnSignal.getLoraAddr(),zhubossOnSignal.getIp(),zhubossOnSignal.getPort(),zhubossOnSignal.getAddr());
						if(meterPO == null){
							log.error("未找到仪表配置,{}",JSON.toJSONString(zhubossOnSignal));
							return;
						}
						MeterKindReadPO meterKindReadPO = meterKindReadPOMapper.selectOneByClause(new QueryClauseBuilder()
								.andEqual(MeterKindReadPO.Fields.METER_KIND_ID, meterPO.getMeterKindId())
								.andEqual(MeterKindReadPO.Fields.TARGET_CODE,targetCode)
						);
						//保存数据库
						LogSignalPO logSignalPO = new LogSignalPO();
						logSignalPO.setId(ObjectId.get());
						logSignalPO.setAppId(meterPO.getAppId());
						logSignalPO.setStationId(meterPO.getStationId());
						logSignalPO.setHappenTime(new Date());
						logSignalPO.setMeterId(meterPO.getId());
						logSignalPO.setMeterKindId(meterPO.getMeterKindId());
						logSignalPO.setMeterName(MeterUtil.getMeterName(meterPO));
						logSignalPO.setTargetCode(targetCode);
						logSignalPO.setTargetName(meterKindReadPO.getTargetName());
						logSignalPO.setReadValue(val);
						logSignalPO.setCreateTime(new Date());
						logSignalPOMapper.insert(logSignalPO);
						//发mq
						SignalMessage signalMessage = BeanMapper.map(logSignalPO,SignalMessage.class);
						sender.sendToMqtt(logSignalPO.getAppId(),logSignalPO.getMeterId(),signalMessage);
					}
				} catch (Exception e) {
					logger.error("保存数据失败" + e.getMessage(),e );
				}
			}
		});
	}


	public void receiveCollectorOfflineMessage(Integer appId, CollectorOfflineMessage collectorOfflineMessage){
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				try{
					sender.sendToMqtt(appId,null, collectorOfflineMessage);
				}catch (Exception e) {
					logger.warn("保存数据失败" + e.getMessage(),e );
				}
			}
		});

	}

	public void receiveMeterOfflineMessage(final String collectorId, final ZhubossMeterOffline zhubossMeterOffline ){
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				try{
					CollectorPO collectorPO = collectorService.getCollectorPO(collectorId);
					//保存到表记录
					MeterPO meterPO = meterService.select(collectorPO.getId(),zhubossMeterOffline.getCom(),zhubossMeterOffline.getLoraAddr(),zhubossMeterOffline.getIp(),zhubossMeterOffline.getPort(),zhubossMeterOffline.getAddr());
					meterService.saveMeterOffline(meterPO.getId());

					//离线日志
					LogLostPO logLostPO = new LogLostPO();
					logLostPO.setId(ObjectId.get());
					logLostPO.setAppId(collectorPO.getAppId());
					logLostPO.setStationId(collectorPO.getStationId());
					logLostPO.setHappenTime(new Date());
					logLostPO.setEventType(OnlineEventType.ONLINE.getCode());
					logLostPO.setDevType(DevType.METER.getCode());
					logLostPO.setMeterKindId(meterPO.getMeterKindId());
					logLostPO.setMeterId(meterPO.getId());
					logLostPO.setMeterName(MeterUtil.getMeterName(meterPO));
					logLostPO.setCreateTime(new Date());
					logLostPOMapper.insert(logLostPO);
					//离线MQ消息
					MeterOfflineMessage meterOffline = new MeterOfflineMessage();
					BeanMapper.copy(logLostPO,meterOffline);
					sender.sendToMqtt(collectorPO.getAppId(),meterPO.getId(),meterOffline);
				}catch (Exception e) {
					logger.warn("保存数据失败" + e.getMessage(),e );
				}
			}
		});

	}

	public void receiveMeterOnlineMessage(String collectorNo,ZhubossMeterOnline zhubossMeterOffline){
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				try{
					CollectorPO collectorPO = collectorService.getCollectorPO(collectorNo);
					//保存到表记录
					MeterPO meterPO = meterService.select(collectorPO.getId(),zhubossMeterOffline.getCom(),zhubossMeterOffline.getLoraAddr(), zhubossMeterOffline.getIp(),zhubossMeterOffline.getPort(),zhubossMeterOffline.getAddr());
					meterService.saveMeterOnline(meterPO.getId());
					//上线日志
					LogLostPO logLostPO = new LogLostPO();
					logLostPO.setId(ObjectId.get());
					logLostPO.setAppId(collectorPO.getAppId());
					logLostPO.setStationId(collectorPO.getStationId());
					logLostPO.setHappenTime(new Date());
					logLostPO.setEventType(OnlineEventType.ONLINE.getCode());
					logLostPO.setDevType(DevType.METER.getCode());
					logLostPO.setMeterKindId(meterPO.getMeterKindId());
					logLostPO.setMeterId(meterPO.getId());
					logLostPO.setMeterName(MeterUtil.getMeterName(meterPO));
					logLostPO.setCreateTime(new Date());
					logLostPOMapper.insert(logLostPO);
					//上线MQ消息
					MeterOfflineMessage meterOfflineMessage = BeanMapper.map(logLostPO, MeterOfflineMessage.class);
					sender.sendToMqtt(collectorPO.getAppId(),meterPO.getId(), meterOfflineMessage);
				}catch (Exception e) {
					logger.warn("保存数据失败" + e.getMessage(),e );
				}
			}
		});

	}


	/**
	 * 处理小数位数，避免浮点数过长
	 * @param meterTypeId
	 * @param values
	 */
	private void processPrecision(Integer meterTypeId,Map<String,Object> values){
		List<MeterKindReadPO> meterKindReadPOList = meterTypeService.getMeterKindTargetByMeterType(meterTypeId);
		for(MeterKindReadPO meterKindReadPO : meterKindReadPOList){
			if(meterKindReadPO.getScale() == null) continue;
			BigDecimal value = TypeUtils.castToBigDecimal(values.get(meterKindReadPO.getTargetCode()));
			if(value == null) continue; // 预警时不是全字段
			BigDecimal newValue = value.setScale(meterKindReadPO.getScale(), RoundingMode.HALF_UP);
			values.put(meterKindReadPO.getTargetCode(),newValue);
		}
	}

	/**
	 * 表达式计算
	 */
	private void processExpression(Integer meterTypeId,Map<String,Object> values){
		ModbusMeterType meterType = (ModbusMeterType)meterTypeFactory.load(meterTypeId);
		for(ReadInfo readInfo : meterType.getReadInfos()){
			for(ProfileInfo profileInfo : readInfo.getProfileInfos()){
				if(profileInfo.getExpression()!=null){
					Object newValue = profileInfo.getExpression().execute(values);
					values.put(profileInfo.getName(),newValue);
				}
			}
		}
	}

}
