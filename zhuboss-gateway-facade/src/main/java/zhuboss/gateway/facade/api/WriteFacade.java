package zhuboss.gateway.facade.api;

import zhuboss.gateway.facade.api.param.WriteParam;
import zhuboss.gateway.facade.vo.ApiResult;

/**
 * 写仪表（遥控、遥调）
 */
public interface WriteFacade {

    ApiResult write(WriteParam writeParam);

}
