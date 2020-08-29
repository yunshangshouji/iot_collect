package zhuboss.gateway.service;

import zhuboss.framework.bean.JsonResponse;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.param.AddCollectorMeterParam;
import zhuboss.gateway.service.param.UpdateCollectorMeterParam;

import java.util.Date;

public interface MeterService {

	void add(AddCollectorMeterParam addCollectorMeterParam);

	void update(UpdateCollectorMeterParam updateCollectorMeterParam);

	void delete(Integer id);

	MeterPO select(Integer collectorId, Integer comPort,Integer loraAddr,String ip,Integer port, Long addr);

	/**
	 * 抄表数据保存
	 */
	void saveRead(Integer collectorMeterId, Date reportTime,Date readTime,boolean success, String content);

	void saveMeterOffline(Integer collectorMeterId);

	void saveMeterOnline(Integer collectorMeterId);

	JsonResponse read(String taskUUID, Integer collectorMeterId, boolean waitResp) throws InterruptedException;

	/**
	 * 设置上报周期字段值
	 * @param meterPO
	 */
	void setCycleSeconds(MeterPO meterPO);

	MeterPO selectCacheableValidateByDevNO(String userName,String password,Integer devId);

	MeterPO getMeterPoByRefId(Integer appId, String refId);
}
