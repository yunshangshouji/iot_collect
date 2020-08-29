package zhuboss.gateway.service;

import zhuboss.gateway.service.vo.AddMeterTypeWriteParam;
import zhuboss.gateway.service.vo.UpdateMeterTypeWriteParam;

public interface MeterTypeWriteService {

    void add(AddMeterTypeWriteParam addMeterTypeWriteParam);

    void update(UpdateMeterTypeWriteParam updateMeterTypeWriteParam);

    void delete(Integer meterTypeWriteId);

}
