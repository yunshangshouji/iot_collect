package zhuboss.gateway.service;

import zhuboss.framework.bean.JsonResponse;
import zhuboss.gateway.po.MeterTypeWritePO;

public interface MeterWriteService {

    JsonResponse write(String taskUUID,Integer meterTypeWriteId,Integer collectorMeterId,String writeData,boolean waitResp) throws InterruptedException;

}
