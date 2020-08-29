package zhuboss.gateway.facade.api;


import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.CollectorParam;
import zhuboss.gateway.facade.vo.ApiResult;

public interface CollectorFacade {
    /**
     * 添加、修改网关
     */
    ApiResult saveCollector(CollectorParam saveParam);

    /**
     * 删除网关
     */
    ApiResult deleteCollector(DeleteParam deleteParam);

}
