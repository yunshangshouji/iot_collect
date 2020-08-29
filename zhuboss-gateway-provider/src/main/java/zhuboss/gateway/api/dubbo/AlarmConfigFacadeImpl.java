package zhuboss.gateway.api.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.facade.api.AlarmConfigFacade;
import zhuboss.gateway.facade.api.param.AlarmConfigParam;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.vo.ApiResult;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.MeterAlarmPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.service.MeterAlarmService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.service.StationService;
import zhuboss.gateway.service.param.*;

@Service(interfaceClass = AlarmConfigFacade.class)
@Component
@Slf4j
public class AlarmConfigFacadeImpl implements AlarmConfigFacade {
    @Autowired
    MeterAlarmService meterAlarmService;
    @Autowired
    MeterService meterService;
    @Autowired
    StationService stationService;

    @Override
    public ApiResult saveAalrmConfig(AlarmConfigParam saveParam) {
        log.info("同步报警条件：{}", JSON.toJSONString(saveParam));
        Integer appId = saveParam.getAppid();
        MeterAlarmPO meterAlarmPO = meterAlarmService.getMeterAlarmPoByRefId(appId,saveParam.getRefId());

        /**
         * 参数
         */
        SaveMeterAlarmParam param;
        if(meterAlarmPO == null){ //新增
            param = new AddMeterAlarmParam();
            BeanMapper.copy(saveParam,param);
        }else{ //更新
            param = new UpdateMeterAlarmParam();
            BeanMapper.copy(saveParam,param);
            ((UpdateMeterAlarmParam)param).setId(meterAlarmPO.getId());
        }

        /**
         * 采集器所属站点
         */
        StationPO stationPO;
        if(StringUtils.hasText(saveParam.getStationRefId())){
            stationPO = stationService.getStationPoByRefId(appId,saveParam.getStationRefId());
            if(stationPO == null){
                String errMsg = "stationRefId("+saveParam.getStationRefId()+")不存在";
                log.error(errMsg);
                return new ApiResult(false,errMsg);
            }
        }else{
            stationPO = stationService.getRootStation(appId);
        }
        param.setStationId(stationPO.getId());

        /**
         * 设备ID转换
         */
        Integer meterId[] = new Integer[saveParam.getMeterRefId().length];
        for(int i=0;i<meterId.length;i++){
            MeterPO meterPO = meterService.getMeterPoByRefId(appId,saveParam.getMeterRefId()[i]);
            if(meterPO == null){
                String errMsg = "meterRefId:"+saveParam.getMeterRefId()[i]+"不存在";
                log.error(errMsg);
                return new ApiResult(false,errMsg);
            }
            meterId[i] = meterPO.getId();
        }
        param.setMeterId(meterId);


        /**
         * 新增、更新
         */
        if(meterAlarmPO == null){
            meterAlarmService.addDevAlarm(saveParam.getAppid(),null,(AddMeterAlarmParam)param);
        }else{
            meterAlarmService.updateDevAlarm(null,(UpdateMeterAlarmParam) param);
        }
        return new ApiResult();
    }

    @Override
    public ApiResult deleteAlarmConfig(DeleteParam deleteParam) {
        log.info("删除采集器：{}", JSON.toJSONString(deleteParam));
        MeterAlarmPO meterAlarmPO = meterAlarmService.getMeterAlarmPoByRefId(deleteParam.getAppid(),deleteParam.getRefId());
        if(meterAlarmPO == null){
            return new ApiResult(false,"关联ID不存在");
        }
        meterAlarmService.deleteDevAlarm(meterAlarmPO.getId());
        return new ApiResult();
    }
}
