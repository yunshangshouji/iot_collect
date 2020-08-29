package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddMeterTypePlcReadParam;
import zhuboss.gateway.service.param.UpdateMeterTypePlcReadParam;

public interface MeterTypePlcReadService {

    void add(AddMeterTypePlcReadParam addMeterTypePlcReadParam);

    void update(UpdateMeterTypePlcReadParam updateMeterTypePlcReadParam);

    void delete(Integer id);
}
