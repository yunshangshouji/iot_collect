package zhuboss.gateway.facade.api;

import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.api.param.MeterParam;
import zhuboss.gateway.facade.vo.ApiResult;

public interface MeterFacade {
    /**
     * 添加、修改仪表
     */
    ApiResult saveMeter(MeterParam saveParam);

    /**
     * 删除仪表
     */
    ApiResult deleteMeter(DeleteParam deleteParam);
}
