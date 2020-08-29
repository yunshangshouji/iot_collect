package zhuboss.gateway.service;

import zhuboss.gateway.po.AppCyclePO;
import zhuboss.gateway.po.CollectorPO;

import java.util.List;

public interface AppCycleService {

    /**
     * 系统设置
     * @param appId
     * @param meterKindId
     * @param meterTypeId
     * @return
     */
    Integer getCycleSeconds(Integer appId, Integer stationId, Integer meterKindId,Integer meterTypeId);

    /**
     * 采集器有设置，则优先采集器
     * @param collectorPO
     * @param appId
     * @param meterKindId
     * @param meterTypeId
     * @return
     */
    Integer getRawCycleSeconds(CollectorPO collectorPO, Integer appId, Integer stationId, Integer meterKindId, Integer meterTypeId);

    /**
     * 智能网关上报周期设置
     * @param collectorPO
     * @param appId
     * @return
     */
    Integer getJsonCycleSeconds(CollectorPO collectorPO,Integer appId,Integer stationId);
    /**
     * 上报优先级排序
     * @param list
     */
    void sortReportLevel(List<AppCyclePO> list);

}
