package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddMeterKindWriteParam;
import zhuboss.gateway.service.param.UpdateMeterKindWriteParam;

public interface MeterKindWriteService {

    void add(AddMeterKindWriteParam addMeterKindTargetParam);

    void update(UpdateMeterKindWriteParam updateMeterKindSignalParam);

    void delete(Integer meterKindSignalId);

}
