package zhuboss.gateway.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.util.StringUtils;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.adapter.ModbusUtil;
import zhuboss.gateway.mapper.MeterTypePOMapper;
import zhuboss.gateway.mapper.MeterTypeWritePOMapper;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.po.MeterTypePO;
import zhuboss.gateway.po.MeterTypeWritePO;
import zhuboss.gateway.service.MeterTypeWriteService;
import zhuboss.gateway.service.vo.AddMeterTypeWriteParam;
import zhuboss.gateway.service.vo.SaveMeterTypeWriteParam;
import zhuboss.gateway.service.vo.UpdateMeterTypeWriteParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuboss.gateway.spring.cache.CacheConstants;
import zhuboss.gateway.util.JavaUtil;

import java.util.Date;

@Service
public class MeterTypeWriteServiceImpl implements MeterTypeWriteService {
    @Autowired
    MeterTypeWritePOMapper meterTypeWritePOMapper;
    @Autowired
    MeterTypePOMapper meterTypePOMapper;

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void add(AddMeterTypeWriteParam addMeterTypeWriteParam) {
        if( StringUtils.hasText(addMeterTypeWriteParam.getDataHex())){
            ModbusUtil.checkWrite(addMeterTypeWriteParam.getCmd(),addMeterTypeWriteParam.getAddr(),addMeterTypeWriteParam.getWriteUnits(),addMeterTypeWriteParam.getWriteByteSize(),addMeterTypeWriteParam.getDataHex());
        }

        MeterTypeWritePO insert = new MeterTypeWritePO();
        BeanMapper.copy(addMeterTypeWriteParam,insert);
        insert.setCreateTime(new Date());
        MeterTypePO meterTypePO = meterTypePOMapper.selectByPK(addMeterTypeWriteParam.getMeterTypeId());
        insert.setAppId(meterTypePO.getAppId());
        meterTypeWritePOMapper.insert(insert);
    }


    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void update(UpdateMeterTypeWriteParam updateMeterTypeWriteParam) {
        if( StringUtils.hasText(updateMeterTypeWriteParam.getDataHex())){
            ModbusUtil.checkWrite(updateMeterTypeWriteParam.getCmd(),updateMeterTypeWriteParam.getAddr(),updateMeterTypeWriteParam.getWriteUnits(),updateMeterTypeWriteParam.getWriteByteSize(),updateMeterTypeWriteParam.getDataHex());
        }

        MeterTypeWritePO update = meterTypeWritePOMapper.selectByPK(updateMeterTypeWriteParam.getId());
        BeanMapper.copy(updateMeterTypeWriteParam,update);
        update.setModifyTime(new Date());
        meterTypeWritePOMapper.updateByPK(update);
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void delete(Integer meterTypeWriteId) {
        //删除记录
        meterTypeWritePOMapper.deleteByPK(meterTypeWriteId);
    }
}
