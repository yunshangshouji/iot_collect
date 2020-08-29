package zhuboss.gateway.api.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.facade.api.CollectorFacade;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.CollectorParam;
import zhuboss.gateway.facade.vo.ApiResult;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.StationService;
import zhuboss.gateway.service.param.AddCollectorParam;
import zhuboss.gateway.service.param.SaveCollectorParam;
import zhuboss.gateway.service.param.UpdateCollectorParam;

@Service(interfaceClass = CollectorFacade.class)
@Component
@Slf4j
public class CollectorFacadeImpl implements CollectorFacade {
    @Autowired
    CollectorService collectorService;
    @Autowired
    StationService stationService;
    @Autowired
    CollectorPOMapper collectorPOMapper;

    @Override
    public ApiResult saveCollector(CollectorParam saveParam) {
        log.info("同步采集器：{}", JSON.toJSONString(saveParam));
        Integer appId = saveParam.getAppid();
        CollectorPO collectorPO = collectorService.getCollectorPoByRefId(appId,saveParam.getRefId());

        /**
         * 参数
         */
        SaveCollectorParam param;
        if(collectorPO == null){ //新增
            param = new AddCollectorParam();
            BeanMapper.copy(saveParam,param);
        }else{ //更新
            param = new UpdateCollectorParam();
            BeanMapper.copy(saveParam,param);
            ((UpdateCollectorParam)param).setId(collectorPO.getId());
        }

        /**
         * 采集器所属站点
         */
        StationPO stationPO;
        if(StringUtils.hasText(saveParam.getStationRefId())){
            stationPO = stationService.getStationPoByRefId(appId,saveParam.getStationRefId());
            if(stationPO == null){
                throw new BussinessException("stationRefId("+saveParam.getStationRefId()+")不存在");
            }
        }else{
            stationPO = stationService.getRootStation(appId);
        }
        param.setStationId(stationPO.getId());

        /**
         * 新增、更新
         */
        if(collectorPO == null){
            collectorService.addCollector(saveParam.getAppid(),(AddCollectorParam)param);
        }else{
            collectorService.updateCollector((UpdateCollectorParam) param);
        }
        return new ApiResult();
    }

    @Override
    public ApiResult deleteCollector(DeleteParam deleteParam) {
        log.info("删除采集器：{}", JSON.toJSONString(deleteParam));
        CollectorPO collectorPO = collectorService.getCollectorPoByRefId(deleteParam.getAppid(),deleteParam.getRefId());
        if(collectorPO == null){
            return new ApiResult(false,"采集器不存在");
        }
        collectorService.deleteById(collectorPO.getId());
        return new ApiResult();
    }

}
