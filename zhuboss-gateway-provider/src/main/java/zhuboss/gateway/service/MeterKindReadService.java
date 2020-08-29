package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddMeterKindReadParam;
import zhuboss.gateway.service.param.UpdateMeterKindReadParam;

import java.util.List;

public interface MeterKindReadService {

    void add(AddMeterKindReadParam addMeterKindReadParam);

    void update(UpdateMeterKindReadParam updateMeterKindTargetParam);

    void delete(Integer meterKindTargetId);

    /**
     * 是否遥信变量
     * @param meterKindId
     * @param targetCode
     * @return
     */
    boolean isSignal(Integer meterKindId,String targetCode);

    void ifPersistFlagsChange(Integer meterKindId,boolean enable,List<Integer> ids);
}
