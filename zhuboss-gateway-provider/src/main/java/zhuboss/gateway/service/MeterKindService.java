package zhuboss.gateway.service;

import zhuboss.gateway.controller.console.param.PersistConfigParam;
import zhuboss.gateway.service.param.AddMeterKindParam;
import zhuboss.gateway.service.param.PersistOption;
import zhuboss.gateway.service.param.UpdateMeterKindParam;

import java.util.Date;
import java.util.Map;

public interface MeterKindService {

    void add(Integer appId,AddMeterKindParam addMeterKindParam);

    void update(UpdateMeterKindParam updateMeterKindParam);

    void delete(Integer meterKindId);

    /**
     * 更新历史保存配置
     * @param persistConfigParam
     */
    void updatePersistConfig(PersistConfigParam persistConfigParam);

    PersistOption loadPersistOptions(Integer meterKindId);

    void doPersist(Integer meterKindId, Integer meterId, Date readTime, Map<String,Object> values);

}
