package zhuboss.gateway.facade.api;

import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.StationParam;
import zhuboss.gateway.facade.vo.ApiResult;

public interface StationFacade {
    /**
     * 添加、修改站点
     */
    ApiResult saveStation(StationParam saveParam);

    /**
     * 删除站点
     */
    ApiResult deleteStation(DeleteParam deleteParam);

}
