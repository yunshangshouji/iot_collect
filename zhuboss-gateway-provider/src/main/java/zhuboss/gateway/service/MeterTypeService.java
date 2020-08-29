package zhuboss.gateway.service;

import zhuboss.gateway.controller.console.param.MeterTypeCopyParam;
import zhuboss.gateway.facade.vo.DataId;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.service.param.AddMeterTypeParam;
import zhuboss.gateway.service.param.UpdateMeterTypeParam;

import java.util.List;

public interface MeterTypeService {

    List<DataId> queryMeterKindVar(Integer meterKindId,Boolean persistFlag);

    List<MeterKindReadPO> getMeterKindTargetByMeterType(Integer meterTypeId);

    void addMeterType(AddMeterTypeParam addMeterTypeParam);

    void updateMeterType(UpdateMeterTypeParam updateMeterTypeParam);

    void deleteMeterType(Integer meterTypeId);

    void copy(MeterTypeCopyParam meterTypeCopyParam);

    List<DataId> queryMeterKindVar(Integer meterKindId);
}
