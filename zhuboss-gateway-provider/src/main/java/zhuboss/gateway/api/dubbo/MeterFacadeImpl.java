package zhuboss.gateway.api.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.facade.api.MeterFacade;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.MeterParam;
import zhuboss.gateway.facade.vo.ApiResult;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.service.param.AddCollectorMeterParam;
import zhuboss.gateway.service.param.SaveCollectorMeterParam;
import zhuboss.gateway.service.param.UpdateCollectorMeterParam;

@Service(interfaceClass = MeterFacade.class)
@Component
@Slf4j
public class MeterFacadeImpl implements MeterFacade {
    @Autowired
    MeterService meterService;
    @Autowired
    CollectorService collectorService;


    @Override
    public ApiResult saveMeter(MeterParam saveParam) {
        log.info("同步设备：{}", JSON.toJSONString(saveParam));
        Integer appId = saveParam.getAppid();
        //TODO 参数校验: interace_type, meter_type_id
        /**
         * 采集器ID
         */
        CollectorPO collectorPO = collectorService.getCollectorPoByRefId(appId,saveParam.getCollectorRefId());
        if(collectorPO == null){
            String failMsg = "采集器不存在,refId:"+saveParam.getCollectorRefId();
            log.error(failMsg);
            return new ApiResult(false,failMsg);
        }

        MeterPO meterPO = meterService.getMeterPoByRefId(appId,saveParam.getRefId());
        /**
         * 参数
         */
        SaveCollectorMeterParam param;
        if(meterPO == null){ //新增
            param = new AddCollectorMeterParam();
            BeanMapper.copy(saveParam,param);
            ((AddCollectorMeterParam) param).setCollectorId(collectorPO.getId());
        }else{ //更新
            param = new UpdateCollectorMeterParam();
            BeanMapper.copy(saveParam,param);
            ((UpdateCollectorMeterParam)param).setId(meterPO.getId());
        }

        /**
         * 新增、更新
         */
        if(meterPO == null){
            meterService.add((AddCollectorMeterParam)param);
        }else{
            meterService.update((UpdateCollectorMeterParam) param);
        }
        return new ApiResult();
    }

    @Override
    public ApiResult deleteMeter(DeleteParam deleteParam) {
        log.info("删除设备：{}", JSON.toJSONString(deleteParam));
        MeterPO meterPO = meterService.getMeterPoByRefId(deleteParam.getAppid(),deleteParam.getRefId());
        meterService.delete(meterPO.getId());
        return new ApiResult();
    }
}
