package zhuboss.gateway.service;

import zhuboss.gateway.dict.ProtocolEnum;

import java.util.Map;

public interface Dlt645Service {

    Map<Integer,Integer> getScaleMap(ProtocolEnum protocolEnum);

}
