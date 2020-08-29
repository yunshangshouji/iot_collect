package zhuboss.gateway.controller.browser;

import io.micrometer.core.instrument.Meter;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.framework.utils.DateUtil;
import zhuboss.gateway.controller.vo.AlarmCollectorVO;
import zhuboss.gateway.controller.vo.AlarmMeterVO;
import zhuboss.gateway.mapper.AlarmOverLimitPOMapper;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.po.*;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.util.MeterUtil;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/browse/alarm")
@Slf4j
public class BrowserAlarmController {
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    AlarmOverLimitPOMapper alarmOverLimitPOMapper;

    @ApiOperation("报警摘要")
    @GetMapping("summary")
    public Map<String,Object> summary(){
        Map<String,Object> result = new HashedMap();
        String showMsg = null;
        //离线网关数量
        QueryClauseBuilder qcb =new QueryClauseBuilder();
        qcb.andEqual(CollectorPO.Fields.APP_ID, UserSession.getAppId());
        qcb.andEqual(CollectorPO.Fields.ONLINE_FLAG,0);
        Integer offlineCollectorCount = collectorPOMapper.selectCountByClause(qcb);
        result.put("offlineCollectorCount",offlineCollectorCount);
        if(offlineCollectorCount >0){
            CollectorPO collectorPO = collectorPOMapper.selectOneByClause(qcb.sort(CollectorPO.Fields.OFFLINE_TIME,ESortOrder.DESC).page(0,1));
            showMsg = collectorPO.getStationName()
                    + (collectorPO.getOfflineTime()==null ? "" : ("," + DateUtil.toDateStr(collectorPO.getOfflineTime(),DateUtil.sdf_yyyyMMddhhmmss)))
                    + "," + "网关离线"+","
                    + (collectorPO.getDevName()==null ? collectorPO.getDevNo() : collectorPO.getDevName());
        }


        //离线设备数量
        qcb =new QueryClauseBuilder();
        qcb.andEqual(MeterPO.Fields.APP_ID, UserSession.getAppId());
        qcb.andEqual(MeterPO.Fields.ONLINE_FLAG,0);
        Integer offlineMeterCount = meterPOMapper.selectCountByClause(qcb);
        result.put("offlineMeterCount",offlineMeterCount);
        if(showMsg == null && offlineMeterCount>0){
            MeterPO meterPO = meterPOMapper.selectOneByClause(qcb.sort(CollectorPO.Fields.OFFLINE_TIME,ESortOrder.DESC).page(0,1));
            showMsg = meterPO.getStationName() +  (meterPO.getOfflineTime()==null ? "" : ("," + DateUtil.toDateStr(meterPO.getOfflineTime(),DateUtil.sdf_yyyyMMddhhmmss)))
                    + "," + "设备离线"+","
                    + MeterUtil.getMeterName(meterPO);
        }

        //越限数量
        qcb =new QueryClauseBuilder();
        qcb.andEqual("app_id", UserSession.getAppId());
        Integer overLimitCount = alarmOverLimitPOMapper.selectCountByClause(qcb);
        result.put("overLimitCount",overLimitCount);
        if(showMsg == null &&overLimitCount>0){
            AlarmOverLimitPO alarmOverLimitPO = alarmOverLimitPOMapper.selectOneByClause(qcb.sort(LogMeter.Fields.CREATE_TIME,ESortOrder.DESC));
            showMsg = alarmOverLimitPO.getStationName() + "," + alarmOverLimitPO.getMeterName() + "," +alarmOverLimitPO.getVarName() + "," + alarmOverLimitPO.getReadValue() +
                    alarmOverLimitPO.getFromValue() != null ? ("越上限"+alarmOverLimitPO.getFromValue()) : ("越下限"+alarmOverLimitPO.getToValue());
        }

        //最新报警
        if(showMsg != null){
            result.put("showMsg",showMsg);
        }

        return result;
    }

    @ApiOperation("离线网关")
    @GetMapping("collector/offline")
    public GridTable<AlarmCollectorVO> queryCollector(@RequestParam(value="start",required = false,defaultValue = "1")  Integer start,
                                                 @RequestParam(value="limit",defaultValue="10") Integer limit
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder();
        qcb.page(start,limit);
        qcb.andEqual(CollectorPO.Fields.APP_ID, UserSession.getAppId());
        qcb.andEqual(CollectorPO.Fields.ONLINE_FLAG,0);
        qcb.sort(CollectorPO.Fields.ID, ESortOrder.DESC);

        List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(qcb);
        List<AlarmCollectorVO> alarmCollectorVOList = BeanMapper.mapList(collectorPOList,AlarmCollectorVO.class);
        Integer count = collectorPOMapper.selectCountByClause(qcb);
        return new GridTable<>(alarmCollectorVOList,count);
    }

    @ApiOperation("离线设备")
    @GetMapping("meter/offline")
    public GridTable<AlarmMeterVO> queryMeter(@RequestParam(value="start",required = false,defaultValue = "1")  Integer start,
                                              @RequestParam(value="limit",defaultValue="10") Integer limit
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder();
        qcb.page(start,limit);
        qcb.andEqual(MeterPO.Fields.APP_ID, UserSession.getAppId());
        qcb.andEqual(MeterPO.Fields.ONLINE_FLAG,0);
        qcb.sort(MeterPO.Fields.OFFLINE_TIME, ESortOrder.DESC);

        List<MeterPO> meterPOList = meterPOMapper.selectByClause(qcb);
        List<AlarmMeterVO> alarmCollectorVOList = BeanMapper.mapList(meterPOList,AlarmMeterVO.class);
        Integer count = meterPOMapper.selectCountByClause(qcb);
        return new GridTable<>(alarmCollectorVOList,count);
    }


    @ApiOperation("遥测越限")
    @GetMapping("meter/overlimit")
    public GridTable<AlarmOverLimitPO> overlimit(@RequestParam(value="start",required = false,defaultValue = "1")  Integer start,
                                              @RequestParam(value="limit",defaultValue="10") Integer limit
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder();
        qcb.page(start,limit);
        qcb.andEqual("app_id", UserSession.getAppId());
        qcb.sort(BaseOverLimitPO.Fields.ID, ESortOrder.DESC);

        List<AlarmOverLimitPO> meterPOList = alarmOverLimitPOMapper.selectByClause(qcb);
        Integer count = alarmOverLimitPOMapper.selectCountByClause(qcb);
        return new GridTable<>(meterPOList,count);
    }
}
