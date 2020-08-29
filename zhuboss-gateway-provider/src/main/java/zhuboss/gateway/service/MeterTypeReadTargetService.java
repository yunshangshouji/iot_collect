package zhuboss.gateway.service;

import zhuboss.gateway.service.vo.AddMeterTypeReadTargetParam;
import zhuboss.gateway.service.vo.UpdateMeterTypeReadTargetParam;

public interface MeterTypeReadTargetService {

    void addMeterTypeTarget(AddMeterTypeReadTargetParam addMeterTypeTargetParam);

    void updateMeterTypeTarget(UpdateMeterTypeReadTargetParam updateMeterTypeTargetParam);

    void deleteMeterTypeTargetId(Integer meterTypeTargetId);

}
