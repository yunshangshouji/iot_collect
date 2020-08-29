package zhuboss.gateway.service;

import com.google.common.collect.ListMultimap;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.po.MeterAlarmPO;
import zhuboss.gateway.po.MeterAlarmPOExt;
import zhuboss.gateway.service.param.AddMeterAlarmParam;
import zhuboss.gateway.service.param.UpdateMeterAlarmParam;
import zhuboss.gateway.service.vo.DoAppCheckResult;

import java.util.Map;

public interface MeterAlarmService {
    /**
     * 某个采集器下所有仪表的越限条件
     * @param collectorId
     * @return
     */
    Map<Integer, ListMultimap<String,String>> map(Integer collectorId);

    void addDevAlarm(Integer appId,Integer userId, AddMeterAlarmParam addDevAlarmParam);

    void updateDevAlarm(Integer userId,UpdateMeterAlarmParam updateDevAlarmParam);

    void deleteDevAlarm(Long id);

    GridTable<MeterAlarmPOExt> query(QueryClauseBuilder queryClauseBuilder);

    /**
     * 光字牌确认
     * @param userId
     * @param devAlarmId
     */
    void doCheck(Integer userId, Long devAlarmId);

    /**
     * 光字牌确认(本站)
     */
    DoAppCheckResult doStationCheck(Integer userId, Integer stationId);

    MeterAlarmPO getMeterAlarmPoByRefId(Integer appId,String refId);

}
