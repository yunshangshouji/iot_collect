package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddMeterTypeReadParam;
import zhuboss.gateway.service.param.UpdateMeterTypeReadParam;

public interface MeterTypeReadService {


    void add(AddMeterTypeReadParam addMeterTypeReadParam);

    void changeOrder(Integer readId,Integer num);

    void update(UpdateMeterTypeReadParam updateMeterTypeReadParam);

    void delete(Integer meterTypeReadId);

}
