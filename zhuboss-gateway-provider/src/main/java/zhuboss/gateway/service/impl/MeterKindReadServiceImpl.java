package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.MeterKindPOMapper;
import zhuboss.gateway.mapper.MeterKindReadPOMapper;
import zhuboss.gateway.mapper.MeterTypeReadTargetPOMapper;
import zhuboss.gateway.po.MeterKindPO;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.po.MeterTypeReadTargetPO;
import zhuboss.gateway.service.DDLService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterKindReadService;
import zhuboss.gateway.service.param.AddMeterKindReadParam;
import zhuboss.gateway.service.param.UpdateMeterKindReadParam;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;
import java.util.List;

@Service
public class MeterKindReadServiceImpl implements MeterKindReadService {
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;
    @Autowired
    MeterKindPOMapper meterKindPOMapper;
    @Autowired
    MeterTypeReadTargetPOMapper meterTypeReadTargetPOMapper;
    @Autowired
    GatewayService gatewayService;
    @Autowired
    DDLService ddlService;
    @Override
    public void add(AddMeterKindReadParam addMeterKindReadParam) {
        MeterKindReadPO insert = new MeterKindReadPO();
        BeanMapper.copy(addMeterKindReadParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        //
        MeterKindPO meterKindPO = meterKindPOMapper.selectByPK(addMeterKindReadParam.getMeterKindId());
        insert.setAppId(meterKindPO.getAppId());
        meterKindReadPOMapper.insert(insert);
        //向网关下载配置
        gatewayService.ifMeterKindChange(addMeterKindReadParam.getMeterKindId());
    }

    @Override
    public void update(UpdateMeterKindReadParam updateMeterKindTargetParam) {
        MeterKindReadPO update = meterKindReadPOMapper.selectByPK(updateMeterKindTargetParam.getId());
        BeanMapper.copy(updateMeterKindTargetParam,update);
        update.setModifyTime(new Date());
        meterKindReadPOMapper.updateByPK(update);

        //向网关下载配置
        gatewayService.ifMeterKindChange(update.getMeterKindId());
    }

    @Override
    @Transactional
    public void delete(Integer meterKindTargetId) {
        MeterKindReadPO meterKindReadPO = meterKindReadPOMapper.selectByPK(meterKindTargetId);

        meterKindReadPOMapper.deleteByPK(meterKindTargetId);
        //TODO 删除meter_type_target表的冗余数据
        meterTypeReadTargetPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterTypeReadTargetPO.Fields.METER_KIND_READ_ID,meterKindTargetId));

        //向网关下载配置
        gatewayService.ifMeterKindChange(meterKindReadPO.getMeterKindId());
    }

    @Override
    @CacheEvict(value= CacheConstants.signal_flag,key="#meterKindId+#targetCode")
    public boolean isSignal(Integer meterKindId, String targetCode) {
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(new QueryClauseBuilder()
            .andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId)
                .andEqual(MeterKindReadPO.Fields.TARGET_CODE,targetCode)
                .andEqual(MeterKindReadPO.Fields.SIGNAL_FLAG,1)
        );
        return meterKindReadPOList.size()==1;
    }

    @Transactional
    @Override
    public void ifPersistFlagsChange(Integer meterKindId, boolean enable, List<Integer> ids) {
        //事务
        for(Integer id : ids){
            MeterKindReadPO meterPO = meterKindReadPOMapper.selectByPK(id);
            meterPO.setPersistFlag(enable ? 1 : 0);
            meterKindReadPOMapper.updateByPK(meterPO);
        }
        ddlService.syncColumns(meterKindId);
    }
}
