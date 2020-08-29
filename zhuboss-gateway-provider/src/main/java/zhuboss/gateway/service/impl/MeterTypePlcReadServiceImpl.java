package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.MeterTypePlcReadPOMapper;
import zhuboss.gateway.po.MeterTypePlcReadPO;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterTypePlcReadService;
import zhuboss.gateway.service.param.AddMeterTypePlcReadParam;
import zhuboss.gateway.service.param.UpdateMeterTypePlcReadParam;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;

@Service
public class MeterTypePlcReadServiceImpl implements MeterTypePlcReadService {
    @Autowired
    MeterTypePlcReadPOMapper meterTypePlcReadPOMapper;
    @Autowired
    GatewayService gatewayService;

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void add(AddMeterTypePlcReadParam addMeterTypePlcReadParam) {
        MeterTypePlcReadPO insert = new MeterTypePlcReadPO();
        BeanMapper.copy(addMeterTypePlcReadParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        meterTypePlcReadPOMapper.insert(insert);
        //向网关下载配置
        gatewayService.ifMeterTypeChange(insert.getMeterTypeId());
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void update(UpdateMeterTypePlcReadParam updateMeterTypePlcReadParam) {
        MeterTypePlcReadPO update = meterTypePlcReadPOMapper.selectByPK(updateMeterTypePlcReadParam.getId());
        BeanMapper.copy(updateMeterTypePlcReadParam,update);
        update.setModifyTime(new Date());
        meterTypePlcReadPOMapper.updateByPK(update);
        //向网关下载配置
        gatewayService.ifMeterTypeChange(update.getMeterTypeId());
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void delete(Integer id) {
        MeterTypePlcReadPO meterTypePlcReadPO = meterTypePlcReadPOMapper.selectByPK(id);
        meterTypePlcReadPOMapper.deleteByPK(id);
        //向网关下载配置
        gatewayService.ifMeterTypeChange(meterTypePlcReadPO.getMeterTypeId());
    }
}
