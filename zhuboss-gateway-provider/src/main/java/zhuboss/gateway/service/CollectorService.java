package zhuboss.gateway.service;

import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.service.param.AddCollectorParam;
import zhuboss.gateway.service.param.UpdateCollectorParam;
import zhuboss.gateway.service.param.UpdateLoraCfgParam;
import zhuboss.gateway.service.vo.CachedCollector;

import java.util.List;

public interface CollectorService {

	void addCollector(Integer appId,AddCollectorParam addCollectorParam);

	void updateCollector(UpdateCollectorParam updateCollectorParam);

	/**
	 * 更改采集器所在站点
	 * 多个参数，是可能DB已经修改，也可能未修改
	 */
	void changeCollectorStation(Integer collectorId,Integer originStationId, Integer newStationId,boolean stationIdUpdated);

	void deleteById(Integer id);

	void deleteCollector(String devNo);

	CollectorPO getCollectorPO(String devNo);

	CachedCollector getCachedCollector(String devNo);

	List<CollectorPO> getAllCollectors();

	void reload();

	/**
	 * 最后活动时间
	 * @param devNo
	 */
	void triggerActive(String devNo);

	CollectorPO getCollectorPoByRefId(Integer appId,String refId);

	/**
	 * 更新LORA配置
	 * @param updateLoraCfgParam
	 */
	void updateLoraCfg(UpdateLoraCfgParam updateLoraCfgParam);

}
