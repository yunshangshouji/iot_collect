package zhuboss.gateway.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.dict.CollectorSrcEnum;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.TxCollectorPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterTreeService;
import zhuboss.gateway.service.param.AddCollectorParam;
import zhuboss.gateway.service.param.UpdateCollectorParam;
import zhuboss.gateway.service.param.UpdateLoraCfgParam;
import zhuboss.gateway.service.vo.CachedCollector;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CollectorServiceImpl implements CollectorService {
	@Autowired
	CollectorPOMapper collectorPOMapper;
	@Autowired
	TxCollectorPOMapper txCollectorPOMapper;
	@Autowired
	MeterPOMapper meterPOMapper;
	@Autowired
	TxMeterPOMapper txMeterPOMapper;
	@Autowired
	MeterTreePOMapper meterTreePOMapper;
	@Autowired
	GatewayService gatewayService;
	@Autowired
	MeterTreeService meterTreeService;

	@Override
	@Transactional
	public void addCollector(Integer appId,AddCollectorParam addCollectorParam) {
		CollectorPO insert = new CollectorPO();
		BeanMapper.copy(addCollectorParam,insert);
		insert.setAppId(appId);
		insert.setSrc(CollectorSrcEnum.HAND.getCode());
		insert.setCreateTime(new Date());
		insert.setModifyTime(insert.getCreateTime());
		collectorPOMapper.insert(insert);
		TxCollectorPO txCollectorPO = new TxCollectorPO();
		txCollectorPO.setCollectorId(insert.getId());
		txCollectorPO.setOnlineFlag(0); //初始不在线
		txCollectorPOMapper.insert(txCollectorPO);
	}

	@Override
	@CacheEvict(value=CacheConstants.collectors,allEntries = true,beforeInvocation=true)
	public void updateCollector(UpdateCollectorParam updateCollectorParam) {
		CollectorPO update = collectorPOMapper.selectByPK(updateCollectorParam.getId());
		Integer originStationId = update.getStationId();
		BeanMapper.copy(updateCollectorParam,update);
		update.setModifyTime(new Date());
		collectorPOMapper.updateByPK(update);
		//设备树同步
		if(!originStationId.equals(updateCollectorParam.getStationId())){
			changeCollectorStation(update.getId(),originStationId,updateCollectorParam.getStationId(),true);
		}
		//刷新设备
		gatewayService.ifCollectorChange(updateCollectorParam.getId(),null);
	}

	@Override
	@Transactional
	public void changeCollectorStation(Integer collectorId,Integer originStationId, Integer newStationId,boolean stationIdUpdated) {
		//这批设备移到新站点的根节点
		meterTreePOMapper.onCollectorStationChanged(collectorId,newStationId);
		//原站点做check，无父节点移到根节点
		meterTreeService.checkStationPid(originStationId);
		if( ! stationIdUpdated){
			CollectorPO collectorPO = collectorPOMapper.selectByPK(collectorId);
			collectorPO.setStationId(newStationId);
			collectorPOMapper.updateByPK(collectorPO);
		}


	}

	@Override
	@Transactional
	@CacheEvict(value=CacheConstants.collectors,allEntries = true)
	public void deleteById(Integer collectorId) {
		txMeterPOMapper.deleteByClause(new QueryClauseBuilder().andSQL("EXISTS(SELECT 1 FROM meter WHERE collector_id = "+ collectorId +" AND tx_meter.`meter_id` = meter.`id`)"));
		meterPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.COLLECTOR_ID,collectorId));
		txCollectorPOMapper.deleteByPK(collectorId);
		collectorPOMapper.deleteByPK(collectorId);
		//TODO his data 表中的历史数据
		//TODO 	确认报警记录清除
	}

	@Override
	@CacheEvict(value=CacheConstants.collectors,allEntries = true)
	@Transactional
	public void deleteCollector(String devNo) {
		CollectorPO collectorPO = this.getCollectorPO(devNo);
		this.deleteById(collectorPO.getId());
	}

	@Override
	public CollectorPO getCollectorPO(String devNo) {
		List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(CollectorPO.Fields.DEV_NO,devNo));
		if(collectorPOList.size()>0){
			return collectorPOList.get(0);
		}
		return null;
	}

	@Override
	@Cacheable(value = CacheConstants.collectors,key="#devNo") //这里的缓存中的 key 就是参数 gwNo
	public CachedCollector getCachedCollector(String devNo) {
		CollectorPO collectorPO = this.getCollectorPO(devNo);
		if(collectorPO == null){
			return null;
		}
		List<MeterPO> meterPOList = meterPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.COLLECTOR_ID,collectorPO.getId()));
		return new CachedCollector(collectorPO, meterPOList);
	}

	@Override
	public List<CollectorPO> getAllCollectors() {
		List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(new QueryClauseBuilder());
		return collectorPOList;
	}

	@Override
	@CacheEvict(value=CacheConstants.collectors,allEntries=true)// 清空 COLLECTORS 缓存
	public void reload() {

	}

	@Override
	public void triggerActive(String devNo) {
		Integer collectorId = this.getCachedCollector(devNo).getCollector().getId();
		Map<String,Object> map = new HashMap<>();
		map.put(TxCollectorPO.Fields.COLLECTOR_ID.name(),collectorId);
		map.put(TxCollectorPO.Fields.LAST_ACTIVE_TIME.name(),new Date());
		txCollectorPOMapper.insertOrUpdate(map);
	}

	@Override
	public CollectorPO getCollectorPoByRefId(Integer appId, String refId) {
		CollectorPO collectorPO = collectorPOMapper.selectOneByClause(new QueryClauseBuilder()
				.andEqual(CollectorPO.Fields.APP_ID,appId)
				.andEqual(CollectorPO.Fields.REF_ID,refId));
		return collectorPO;
	}

	@Override
	@CacheEvict(value=CacheConstants.collectors,allEntries=true)// 清空 COLLECTORS 缓存
	public void updateLoraCfg(UpdateLoraCfgParam updateLoraCfgParam) {
		CollectorPO collectorPO = collectorPOMapper.selectByPK(updateLoraCfgParam.getCollectorId());
		BeanMapper.copy(updateLoraCfgParam,collectorPO);
		collectorPO.setModifyTime(new Date());
		collectorPOMapper.updateByPK(collectorPO);
		gatewayService.ifCollectorChange(updateLoraCfgParam.getCollectorId(),null);
	}

}
