package zhuboss.gateway.facade.api;

import zhuboss.gateway.facade.api.param.AlarmConfigParam;
import zhuboss.gateway.facade.api.param.DeleteParam;
import zhuboss.gateway.facade.vo.ApiResult;

public interface AlarmConfigFacade {

    /**
     * 添加、修改报警
     */
    ApiResult saveAalrmConfig(AlarmConfigParam saveParam);

    /**
     * 删除报警
     */
    ApiResult deleteAlarmConfig(DeleteParam deleteParam);

}
