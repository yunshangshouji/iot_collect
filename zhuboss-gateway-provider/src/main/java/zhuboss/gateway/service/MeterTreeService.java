package zhuboss.gateway.service;

import zhuboss.gateway.controller.console.param.ChangePidParam;
import zhuboss.gateway.service.param.AddMeterTreeParam;
import zhuboss.gateway.service.param.UpdateMeterTreeParam;

public interface MeterTreeService {

    void add(Integer appId, AddMeterTreeParam addMeterTreeParam);

    void update(Integer appId,UpdateMeterTreeParam updateMeterTreeParam);

    void deleteById(Integer appId,Integer id);

    /**
     * 纠正设备树不存在的父ID
     * @param stationId
     */
    void checkStationPid(Integer stationId);

    void changePid(Integer appId, ChangePidParam changePidParam);

}
