package zhuboss.gateway.api.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.facade.api.StationFacade;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.StationParam;
import zhuboss.gateway.facade.vo.ApiResult;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.service.StationService;
import zhuboss.gateway.service.param.AddStationParam;
import zhuboss.gateway.service.param.SaveStationParam;
import zhuboss.gateway.service.param.UpdateStationParam;

@Service(interfaceClass = StationFacade.class)
@Component
@Slf4j
public class StationFacadeImpl implements StationFacade {
    @Autowired
    StationService stationService;

    @Override
    public ApiResult saveStation(StationParam saveParam) {
        log.info("同步站点：{}", JSON.toJSONString(saveParam));
        Integer appId = saveParam.getAppid();
        StationPO stationPO = stationService.getStationPoByRefId(appId,saveParam.getRefId());

        /**
         * 参数
         */
        SaveStationParam param;
        if(stationPO == null){ //新增
            param = new AddStationParam();
            BeanMapper.copy(saveParam,param);
        }else{ //更新
            param = new UpdateStationParam();
            BeanMapper.copy(saveParam,param);
            ((UpdateStationParam)param).setId(stationPO.getId());
        }

        if(saveParam.getParentRefId() != null){
            StationPO stationPOParent = stationService.getStationPoByRefId(saveParam.getAppid(),saveParam.getParentRefId());
            if(stationPOParent == null){
                String errMsg = "ParentRefId:"+saveParam.getParentRefId()+"不存在";
                log.error(errMsg);
                return new ApiResult(false,errMsg);
            }
            param.setPid(stationPOParent.getId());
        }else{
            param.setPid( null ); //root station
        }

        /**
         * 新增、更新
         */
        if(stationPO == null){
            stationService.add(saveParam.getAppid(),(AddStationParam) param);
        }else{
            stationService.update(saveParam.getAppid(),(UpdateStationParam) param);
        }
        return new ApiResult();
    }

    @Override
    public ApiResult deleteStation(DeleteParam deleteParam) {
        log.info("删除站点：{}", JSON.toJSONString(deleteParam));
        StationPO stationPO = stationService.getStationPoByRefId(deleteParam.getAppid(),deleteParam.getRefId());
        if(stationPO == null){
            return new ApiResult(false,"站点关联ID不存在:"+deleteParam.getRefId());
        }
        stationService.delete(deleteParam.getAppid(),stationPO.getId());
        return new ApiResult();
    }
}
