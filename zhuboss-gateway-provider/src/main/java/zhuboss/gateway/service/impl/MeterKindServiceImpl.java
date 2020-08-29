package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.controller.console.param.PersistConfigParam;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.MeterKindPO;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.po.MeterTypePO;
import zhuboss.gateway.service.DDLService;
import zhuboss.gateway.service.MeterKindService;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.service.param.AddMeterKindParam;
import zhuboss.gateway.service.param.PersistOption;
import zhuboss.gateway.service.param.UpdateMeterKindParam;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MeterKindServiceImpl implements MeterKindService {
    @Autowired
    MeterKindPOMapper meterKindPOMapper;
    @Autowired
    MeterKindWritePOMapper meterKindWritePOMapper;
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;
    @Autowired
    MeterTypePOMapper meterTypePOMapper;
    @Autowired
    MeterTypeService meterTypeService;
    @Autowired
    DDLService ddlService;
    @Autowired
    HisDataMapper hisDataMapper;

    @Override
    public void add(Integer appId,AddMeterKindParam addMeterKindParam) {
        MeterKindPO insert = new MeterKindPO();
        BeanMapper.copy(addMeterKindParam,insert);
        insert.setAppId(appId);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        meterKindPOMapper.insert(insert);
    }

    @Override
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void update(UpdateMeterKindParam updateMeterKindParam) {
        MeterKindPO update = meterKindPOMapper.selectByPK(updateMeterKindParam.getId());
        BeanMapper.copy(updateMeterKindParam,update);
        update.setModifyTime(new Date());
        meterKindPOMapper.updateByPK(update);
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.meter_type,allEntries = true)
    public void delete(Integer meterKindId) {
        //删除仪表型号
        List<MeterTypePO> meterTypePOList = meterTypePOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTypePO.Fields.METER_KIND_ID,meterKindId));
        for(MeterTypePO meterTypePO : meterTypePOList){
            meterTypeService.deleteMeterType(meterTypePO.getId());
        }
        //删除仪表定义
        meterKindReadPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId));
        meterKindWritePOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterKindWritePO.Fields.METER_KIND_ID,meterKindId));
        meterKindPOMapper.deleteByPK(meterKindId);

    }

    @Override
    @CacheEvict(value = CacheConstants.persist_options,key = "#persistConfigParam.meterKindId")
    public void updatePersistConfig(PersistConfigParam persistConfigParam) {
        MeterKindPO meterKindPO = meterKindPOMapper.selectByPK(persistConfigParam.getMeterKindId());
        BeanMapper.copy(persistConfigParam,meterKindPO);
        meterKindPOMapper.updateByPK(meterKindPO);

        if(persistConfigParam.getPersistFlag() == 0){
            ddlService.dropIfExists(persistConfigParam.getMeterKindId());
        }else{
            ddlService.syncColumns(persistConfigParam.getMeterKindId());
        }

    }

    @Override
    @Cacheable(value = CacheConstants.persist_options,key = "#meterKindId")
    public PersistOption loadPersistOptions(Integer meterKindId) {
        MeterKindPO meterKindPO = meterKindPOMapper.selectByPK(meterKindId);
        if(meterKindPO.getPersistFlag() == null || meterKindPO.getPersistFlag() == 0){
            return null;
        }
        PersistOption persistOption = new PersistOption();
        persistOption.setPersistInterval(meterKindPO.getPersistInterval());
        persistOption.setPersistUnit(meterKindPO.getPersistUnit());
        persistOption.setPersistDays(meterKindPO.getPersistDays());
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId).andEqual(MeterKindReadPO.Fields.PERSIST_FLAG,1));
        if(meterKindReadPOList.size() == 0){
            return null;
        }
        for(MeterKindReadPO meterKindReadPO : meterKindReadPOList){
            persistOption.getCols().add( ddlService.getColName(meterKindReadPO.getId()));
            persistOption.getTargetCodes().add(meterKindReadPO.getTargetCode());
        }
        return persistOption;
    }

    @Override
    public void doPersist(Integer meterKindId, Integer meterId, Date readTime, Map<String, Object> values) {
        if(values ==null || values.size()==0){
            return;
        }
        PersistOption persistOption = this.loadPersistOptions(meterKindId);
        if(persistOption == null){
            return;
        }

        //历史记录，最近一条是否已经存在
        long ts = readTime.getTime();
        int period = persistOption.getPersistSeconds()*1000;
        Date saveTime = new Date((ts / period) * period);
        boolean exists = hisDataMapper.checkRecordExists(ddlService.getTableName(meterKindId),saveTime,meterId);
        if(exists){
            return;
        }

        //写入
        List<Object> valueList = new ArrayList<>();
        for(int i=0;i<persistOption.getCols().size();i++){
            String col = persistOption.getCols().get(i);
            valueList.add(values.get(persistOption.getTargetCodes().get(i)));
        }
        hisDataMapper.insert(ddlService.getTableName(meterKindId),saveTime,meterId,persistOption.getCols(),valueList);
    }

}
