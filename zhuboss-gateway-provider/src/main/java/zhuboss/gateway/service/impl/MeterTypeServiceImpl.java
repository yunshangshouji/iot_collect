package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.controller.console.param.MeterTypeCopyParam;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.facade.vo.DataId;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.service.param.AddMeterTypeParam;
import zhuboss.gateway.service.param.SaveMeterTypeParam;
import zhuboss.gateway.service.param.UpdateMeterTypeParam;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.*;

@Service
public class MeterTypeServiceImpl implements MeterTypeService {
    @Autowired
    MeterTypePOMapper meterTypePOMapper;
    @Autowired
    MeterKindPOMapper meterKindPOMapper;
    @Autowired
    MeterTypeDltPOMapper meterTypeDltPOMapper;
    @Autowired
    MeterTypePlcReadPOMapper meterTypePlcReadPOMapper;
    @Autowired
    MeterTypeReadTargetPOMapper meterTypeReadTargetPOMapper;
    @Autowired
    MeterTypeWritePOMapper meterTypeWritePOMapper;
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;
    @Autowired
    MeterTypeReadPOMapper meterTypeReadPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    GatewayService gatewayService;


    @Override
    public List<DataId> queryMeterKindVar(Integer meterKindId,Boolean persistFlag) {
        QueryClauseBuilder qcb = new QueryClauseBuilder()
                .sort(MeterKindReadPO.Fields.TARGET_CODE);
        if(meterKindId != null){
            qcb.andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId);
        }
        if(persistFlag!=null){
            qcb.andEqual(MeterKindReadPO.Fields.PERSIST_FLAG, (persistFlag==true? 1 : 0));
        }
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(qcb);
        List<DataId> results = new ArrayList<>();
        for(MeterKindReadPO meterKindReadPO : meterKindReadPOList){
            results.add(new DataId(meterKindReadPO.getId(),meterKindReadPO.getUnit(),meterKindReadPO.getMeterKind(), meterKindReadPO.getTargetCode(),meterKindReadPO.getTargetName()));
        }
        return results;
    }

    @Override
    public List<MeterKindReadPO> getMeterKindTargetByMeterType(Integer meterTypeId) {
        MeterTypePO meterTypePO = meterTypePOMapper.selectByPK(meterTypeId);
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterTypePO.getMeterKindId()));
        return meterKindReadPOList;
    }

    @Override
    public void addMeterType(AddMeterTypeParam addMeterTypeParam) {
        MeterTypePO insert = new MeterTypePO();
        BeanMapper.copy(addMeterTypeParam,insert);
        insert.setCreateTime(new Date());

        MeterKindPO meterKindPO = meterKindPOMapper.selectByPK(addMeterTypeParam.getMeterKindId());
        insert.setAppId(meterKindPO.getAppId());
        if(meterKindPO.getPlcFlag() == 0){
            commonCheck(addMeterTypeParam);
        }
        meterTypePOMapper.insert(insert);
    }

    @Override
    public void updateMeterType(UpdateMeterTypeParam updateMeterTypeParam) {
        MeterKindPO meterKindPO = meterKindPOMapper.selectByPK(updateMeterTypeParam.getMeterKindId());
        if(meterKindPO.getPlcFlag() == 0){
            commonCheck(updateMeterTypeParam);
        }

        MeterTypePO oldPO = meterTypePOMapper.selectByPK(updateMeterTypeParam.getId());
        BeanMapper.copy(updateMeterTypeParam,oldPO);
        oldPO.setModifyTime(new Date());
        meterTypePOMapper.updateByPK(oldPO);
        gatewayService.ifMeterTypeChange(updateMeterTypeParam.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConstants.meter_type, key = "#meterTypeId")
    public void deleteMeterType(Integer meterTypeId) {
        //已经使用的仪表
        meterPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.METER_TYPE_ID,meterTypeId));
        //TODO 采集器缓存依然存在

        //
        meterTypeDltPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterTypeDltPO.Fields.METER_TYPE_ID,meterTypeId));
        meterTypePlcReadPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterTypePlcReadPO.Fields.METER_TYPE_ID,meterTypeId));
        meterTypeReadTargetPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterTypeReadTargetPO.Fields.METER_TYPE_ID,meterTypeId));
        meterTypeReadPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterTypeReadPO.Fields.METER_TYPE_ID,meterTypeId));
        meterTypeWritePOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterTypeWritePO.Fields.METER_TYPE_ID,meterTypeId));
        meterTypePOMapper.deleteByPK(meterTypeId);
    }

    @Override
    @Transactional
    public void copy(MeterTypeCopyParam meterTypeCopyParam) {
        Integer meterTypeId = meterTypeCopyParam.getMeterTypeId();
        String typeName = meterTypeCopyParam.getTypeName();
        MeterTypePO meterTypePO = meterTypePOMapper.selectByPK(meterTypeId);
        List<MeterTypeReadTargetPO> meterTypeReadTargetPOList = meterTypeReadTargetPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTypeReadTargetPO.Fields.METER_TYPE_ID,meterTypeId));
        List<MeterTypeWritePO> meterTypeWritePOList = meterTypeWritePOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTypeWritePO.Fields.METER_TYPE_ID,meterTypeId));
        List<MeterTypeReadPO> meterTypeReadPOList = meterTypeReadPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterTypeReadPO.Fields.METER_TYPE_ID,meterTypeId));

        //
        MeterTypePO meterTypePO1 = BeanMapper.map(meterTypePO,MeterTypePO.class);
        meterTypePO1.setId(null);
        meterTypePO1.setTypeName(typeName);
        meterTypePO1.setCreateTime(new Date());
        meterTypePO1.setModifyTime(new Date());
        meterTypePOMapper.insert(meterTypePO1);
        //
        Map<Integer,Integer> oldNewReadIdMap = new HashMap<>();
        for(MeterTypeReadPO meterTypeReadPO : meterTypeReadPOList){
            MeterTypeReadPO insert = BeanMapper.map(meterTypeReadPO,MeterTypeReadPO.class);
            insert.setId(null);
            insert.setMeterTypeId(meterTypePO1.getId());
            insert.setCreateTime(new Date());
            insert.setModifyTime(insert.getCreateTime());
            meterTypeReadPOMapper.insert(insert);
            oldNewReadIdMap.put(meterTypeReadPO.getId(),insert.getId());
        }
        //
        for(MeterTypeReadTargetPO meterTypeReadTargetPO : meterTypeReadTargetPOList){
            MeterTypeReadTargetPO insert = BeanMapper.map(meterTypeReadTargetPO, MeterTypeReadTargetPO.class);
            insert.setId(null);
            insert.setMeterTypeId(meterTypePO1.getId());
            insert.setReadId(oldNewReadIdMap.get(meterTypeReadTargetPO.getReadId()));
            insert.setCreateTime(new Date());
            insert.setModifyTime(new Date());
            meterTypeReadTargetPOMapper.insert(insert);
        }
        //
        for(MeterTypeWritePO meterTypeWritePO : meterTypeWritePOList){
            MeterTypeWritePO insert = BeanMapper.map(meterTypeWritePO, MeterTypeWritePO.class);
            insert.setId(null);
            insert.setMeterTypeId(meterTypePO1.getId());
            insert.setCreateTime(new Date());
            insert.setModifyTime(new Date());
            meterTypeWritePOMapper.insert(insert);
        }
    }

    @Override
    public List<DataId> queryMeterKindVar(Integer meterKindId) {
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId)
                .sort("target_code"));
        List<DataId> results = new ArrayList<>();
        for(MeterKindReadPO meterKindTargetPO : meterKindReadPOList){
            results.add(new DataId(meterKindTargetPO.getId(),meterKindTargetPO.getUnit(),meterKindTargetPO.getMeterKind(), meterKindTargetPO.getTargetCode(),meterKindTargetPO.getTargetName()));
        }
        return results;
    }

    void commonCheck(SaveMeterTypeParam saveMeterTypeParam){
        ProtocolEnum protocol = ProtocolEnum.valueOf(saveMeterTypeParam.getProtocol());
    }
}
