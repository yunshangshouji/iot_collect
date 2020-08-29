package zhuboss.gateway.api.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import zhuboss.gateway.facade.api.MeterTypeFacade;
import zhuboss.gateway.facade.vo.*;
import zhuboss.gateway.mapper.MeterKindWritePOMapper;
import zhuboss.gateway.mapper.MeterKindReadPOMapper;
import zhuboss.gateway.mapper.MeterTypePOMapper;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.po.MeterTypePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;

import java.util.ArrayList;
import java.util.List;
@Service(interfaceClass = MeterTypeFacade.class)
@Component
@Slf4j
public class MeterTypeFacadeImpl implements MeterTypeFacade {
    @Autowired
    MeterTypePOMapper meterTypePOMapper;
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;
    @Autowired
    MeterKindWritePOMapper meterKindWritePOMapper;


    @Override
    public List<MeterType> getAllMeterType() {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual("alive_flag","1").sort("id");
        List<MeterTypePO> list = meterTypePOMapper.selectByClause(qcb);
        List<MeterType> results = new ArrayList<>();
        for(MeterTypePO meterTypePO : list){
            MeterType item = new MeterType();
            item.setId(meterTypePO.getId());
            item.setMeterKind(meterTypePO.getMeterKind());
            item.setTypeName(meterTypePO.getTypeName());
            results.add(item);
        }
        return results;
    }

    @Override
    public List<MeterKindRead> getAllMeterKindTarget() {
        QueryClauseBuilder qcb = new QueryClauseBuilder().sort("id");
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(qcb);
        List<MeterKindRead> results = new ArrayList<>();
        for(MeterKindReadPO meterKindReadPO : meterKindReadPOList){
            MeterKindRead item = new MeterKindRead();
            item.setId(meterKindReadPO.getId());
            item.setMeterKind(meterKindReadPO.getMeterKind());
            item.setTargetCode(meterKindReadPO.getTargetCode());
            item.setTargetName(meterKindReadPO.getTargetName());
            item.setUnit(meterKindReadPO.getUnit());
            results.add(item);
        }
        return results;
    }

    @Override
    public List<MeterKindWrite> getAllMeterKindSignal() {
        QueryClauseBuilder qcb = new QueryClauseBuilder().sort("id");
        List<MeterKindWritePO> meterKindWritePOList = meterKindWritePOMapper.selectByClause(qcb);
        List<MeterKindWrite> results = new ArrayList<>();
        for(MeterKindWritePO meterKindWritePO : meterKindWritePOList){
            MeterKindWrite item = new MeterKindWrite();
            item.setId(meterKindWritePO.getId());
            item.setMeterKind(meterKindWritePO.getMeterKind());
            item.setTargetCode(meterKindWritePO.getTargetCode());
            item.setTargetName(meterKindWritePO.getTargetName());
            results.add(item);
        }
        return results;
    }

//    @Override
    public List<Item> queryMeterType(String meterKind) {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual("alive_flag","1").sort("id", ESortOrder.DESC);
        if(StringUtils.hasText(meterKind)){
            qcb.andEqual("meter_type",meterKind);
        }
        List<MeterTypePO> list = meterTypePOMapper.selectByClause(qcb);
        List<Item> results = new ArrayList<>();
        for(MeterTypePO meterTypePO : list){
            results.add(new Item(meterTypePO.getId()+"",meterTypePO.getId()+"."+meterTypePO.getTypeName()));
        }
        return results;
    }

//    @Override
    public List<DataId> queryMeterKindTarget(String meterKind) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        if(StringUtils.hasText(meterKind)){
            qcb.andEqual("meter_kind",meterKind);
        }
        List<MeterKindReadPO> meterKindReadPOList = meterKindReadPOMapper.selectByClause(qcb);
        List<DataId> results = new ArrayList<>();
        for(MeterKindReadPO meterKindReadPO : meterKindReadPOList){
            results.add(new DataId(meterKindReadPO.getId(), meterKindReadPO.getUnit(), meterKindReadPO.getMeterKind(), meterKindReadPO.getTargetCode(), meterKindReadPO.getTargetName()));
        }
        List<MeterKindWritePO> meterKindWritePOList = meterKindWritePOMapper.selectByClause(qcb);
        for(MeterKindWritePO meterKindWritePO : meterKindWritePOList){
            results.add(new DataId(meterKindWritePO.getId(),null, meterKindWritePO.getMeterKind(), meterKindWritePO.getTargetCode(), meterKindWritePO.getTargetName()));
        }
        return results;
    }

    @Override
    public List<SignalItem> queryMeterKindSignal(String meterKind,Integer meterTypeId,Integer alarmBit) {
        QueryClauseBuilder queryClauseBuilder = new QueryClauseBuilder()
                .andEqual("meter_kind",meterKind)
                .sort("target_code");
        if(alarmBit !=null){
            queryClauseBuilder.andEqual("alarm_bit",alarmBit);
        }
        if(meterTypeId != null){
            queryClauseBuilder.andSQL("EXISTS(SELECT 1 FROM meter_type_write WHERE meter_type_id = "+meterTypeId+" AND meter_kind = meter_kind_write.`meter_kind` AND target_code = meter_kind_write.target_code)");
        }
        List<MeterKindWritePO> meterKindWritePOList = meterKindWritePOMapper.selectByClause(queryClauseBuilder);
        List<SignalItem> results = new ArrayList<>();
        for(MeterKindWritePO meterKindWritePO : meterKindWritePOList){
            results.add(new SignalItem(meterKindWritePO.getTargetCode(), meterKindWritePO.getTargetName()));
        }
        return results;
    }

}
