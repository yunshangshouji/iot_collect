package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.MeterKindWritePOMapper;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.service.MeterKindWriteService;
import zhuboss.gateway.service.param.AddMeterKindWriteParam;
import zhuboss.gateway.service.param.UpdateMeterKindWriteParam;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;

@Service
public class MeterKindWriteServiceImpl implements MeterKindWriteService {
    @Autowired
    MeterKindWritePOMapper meterKindWritePOMapper;

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void add(AddMeterKindWriteParam addMeterKindSignalParam) {
        MeterKindWritePO insert = new MeterKindWritePO();
        BeanMapper.copy(addMeterKindSignalParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        meterKindWritePOMapper.insert(insert);
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void update(UpdateMeterKindWriteParam updateMeterKindSignalParam) {
        MeterKindWritePO update = meterKindWritePOMapper.selectByPK(updateMeterKindSignalParam.getId());
        BeanMapper.copy(updateMeterKindSignalParam,update);
        update.setModifyTime(new Date());
        meterKindWritePOMapper.updateByPK(update);
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void delete(Integer meterKindSignalId) {
        meterKindWritePOMapper.deleteByPK(meterKindSignalId);
    }
}
