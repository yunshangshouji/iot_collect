package zhuboss.gateway.service;

import zhuboss.gateway.controller.console.param.ChangePidParam;
import zhuboss.gateway.po.StationPO;
import zhuboss.gateway.service.param.AddStationParam;
import zhuboss.gateway.service.param.UpdateStationParam;

public interface StationService {

    StationPO getRootStation(Integer appId);

    void add(Integer appId,AddStationParam addStationParam);

    void update(Integer appId,UpdateStationParam updateStationParam);

    void delete(Integer appId,Integer stationId);

    void changePid(Integer appId, ChangePidParam changePidParam);

    StationPO getStationPoByRefId(Integer appId,String refId);

}
