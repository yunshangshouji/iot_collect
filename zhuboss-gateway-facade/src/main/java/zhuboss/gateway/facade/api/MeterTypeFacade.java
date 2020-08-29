package zhuboss.gateway.facade.api;

import zhuboss.gateway.facade.vo.*;

import java.util.List;

public interface MeterTypeFacade {

    List<MeterType> getAllMeterType();

    List<MeterKindRead> getAllMeterKindTarget();

    List<MeterKindWrite> getAllMeterKindSignal();

    /**
     * 查找表类型（字典）
     * @param meterKind 仪表类别，空为全部
     * @return
     */
    List<Item> queryMeterType(String meterKind);

    /**
     * 查询类别字段
     * @param meterKind
     * @return
     */
//    List<DataId> queryMeterKindTarget(String meterKindId);

    List<SignalItem> queryMeterKindSignal(String meterKind, Integer meterTypeId, Integer alarmBit);




}
