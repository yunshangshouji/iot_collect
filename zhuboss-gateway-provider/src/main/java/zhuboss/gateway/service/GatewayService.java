package zhuboss.gateway.service;

import zhuboss.framework.bean.JsonResponse;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo.ZhubossMeta;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo.ZhubossRegisterParam;

public interface GatewayService {
    /**
     * 注册
     * @param zhubossRegisterParam
     * @return
     */
    JsonResponse checkRegisterResult(ZhubossRegisterParam zhubossRegisterParam);

    /**
     * 获取设备下发抄表配置
     * @param devNo
     * @return
     */
    ZhubossMeta getDownMeta(String devNo);
    /**
     * 向网关下发抄表配置
     * @param devNo
     */
    void doDownMeta(String devNo);

    /**
     * 如果是智能网关的元数据发生变更，需要主动下发给设备
     * @param collectorId
     * @param devNo
     */
    void ifCollectorChange(Integer collectorId, String devNo);


    void ifMeterKindChange(Integer meterKindId);
    /**
     * 仪表元数据变化
     * @param meterTypeId
     */
    void ifMeterTypeChange(Integer meterTypeId);

    void ifAppCycleChange(Integer appId);

}
