package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.MeterTypeDltPOMapper;
import zhuboss.gateway.po.MeterTypeDltPO;
import zhuboss.gateway.service.MeterTypeDltService;
import zhuboss.gateway.service.param.AddMeterTypeDltParam;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;

@Service
public class MeterTypeDltServiceImpl implements MeterTypeDltService {
    @Autowired
    MeterTypeDltPOMapper meterTypeDltPOMapper;

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void add(AddMeterTypeDltParam addMeterTypeDltParam) {
        MeterTypeDltPO insert = new MeterTypeDltPO();
        BeanMapper.copy(addMeterTypeDltParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        meterTypeDltPOMapper.insert(insert);
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void delete(Integer id) {
        meterTypeDltPOMapper.deleteByPK(id);
    }
}
