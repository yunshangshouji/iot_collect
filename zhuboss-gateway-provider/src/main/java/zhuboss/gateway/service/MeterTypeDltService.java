package zhuboss.gateway.service;

import zhuboss.gateway.service.param.AddMeterTypeDltParam;

public interface MeterTypeDltService {

    void add(AddMeterTypeDltParam addMeterTypeDltParam);

    void delete(Integer id);
}
