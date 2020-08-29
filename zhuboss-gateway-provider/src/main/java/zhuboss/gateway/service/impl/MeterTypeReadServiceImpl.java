package zhuboss.gateway.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.mapper.MeterTypePOMapper;
import zhuboss.gateway.mapper.MeterTypeReadPOMapper;
import zhuboss.gateway.mapper.MeterTypeReadTargetPOMapper;
import zhuboss.gateway.po.MeterTypePO;
import zhuboss.gateway.po.MeterTypeReadPO;
import zhuboss.gateway.po.MeterTypeReadTargetPO;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterTypeReadService;
import zhuboss.gateway.service.param.AddMeterTypeReadParam;
import zhuboss.gateway.service.param.UpdateMeterTypeReadParam;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;
import java.util.List;

@Service
public class MeterTypeReadServiceImpl implements MeterTypeReadService {
    @Autowired
    MeterTypeReadPOMapper meterTypeReadPOMapper;
    @Autowired
    MeterTypePOMapper meterTypePOMapper;
    @Autowired
    MeterTypeReadTargetPOMapper meterTypeReadTargetPOMapper;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    GatewayService gatewayService;

    @Override
    public void add(AddMeterTypeReadParam addMeterTypeReadParam) {
        MeterTypeReadPO insert = new MeterTypeReadPO();
        BeanMapper.copy(addMeterTypeReadParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        MeterTypePO meterTypePO = meterTypePOMapper.selectByPK(addMeterTypeReadParam.getMeterTypeId());
        insert.setAppId(meterTypePO.getAppId());
        Integer count = meterTypeReadPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(MeterTypeReadPO.Fields.METER_TYPE_ID,addMeterTypeReadParam.getMeterTypeId()));
        insert.setSeq(count + 1);
        meterTypeReadPOMapper.insert(insert);
        //向网关下载配置
        gatewayService.ifMeterTypeChange(insert.getMeterTypeId());
    }


    public void changeOrder(Integer readId,Integer num){
        Assert.isTrue(num ==1 || num ==-1);
        MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(readId);
        List<MeterTypeReadPO> meterTypeReadPOList = meterTypeReadPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterTypeReadPO.Fields.METER_TYPE_ID,meterTypeReadPO.getMeterTypeId())
                .sort(MeterTypeReadPO.Fields.SEQ)
        );
        int index = 0;
        for(int i=0;i<meterTypeReadPOList.size();i++){
            if(meterTypeReadPOList.get(i).getId().equals(readId)){
                index = i;
                break;
            }
        }
        //
        if(
                (index ==0 && num == -1) || (index == meterTypeReadPOList.size()-1 && num ==1)
        ){ //首行上升,尾行下降
            return;
        }
        //
        if(num == -1){
            MeterTypeReadPO pre = meterTypeReadPOList.get(index-1);
            pre.setSeq(pre.getSeq()+1);
            meterTypeReadPOMapper.updateByPK(pre);
            meterTypeReadPO.setSeq(meterTypeReadPO.getSeq() - 1);
            meterTypeReadPOMapper.updateByPK(meterTypeReadPO);
        }else if(num == 1){
            MeterTypeReadPO after = meterTypeReadPOList.get(index+1);
            after.setSeq(after.getSeq()-1);
            meterTypeReadPOMapper.updateByPK(after);
            meterTypeReadPO.setSeq(meterTypeReadPO.getSeq() + 1);
            meterTypeReadPOMapper.updateByPK(meterTypeReadPO);
        }


    }

    @Override
    public void update(UpdateMeterTypeReadParam updateMeterTypeReadParam) {
        MeterTypeReadPO update = meterTypeReadPOMapper.selectByPK(updateMeterTypeReadParam.getId());
        BeanMapper.copy(updateMeterTypeReadParam,update);
        update.setModifyTime(new Date());
//        if(updateMeterTypeReadParam.getCmd() == 1 || updateMeterTypeReadParam.getCmd()==2){
//            update.setStartAddr(Integer.parseInt(updateMeterTypeReadParam.getStartAddrHex(),16));
//        }
        meterTypeReadPOMapper.updateByPK(update);
        //向网关下载配置
        gatewayService.ifMeterTypeChange(update.getMeterTypeId());
    }

    @Override
    @Transactional
    public void delete(Integer meterTypeReadId) {
        MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(meterTypeReadId);
        meterTypeReadTargetPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterTypeReadTargetPO.Fields.READ_ID,meterTypeReadId));
        meterTypeReadPOMapper.deleteByPK(meterTypeReadId);
        //向网关下载配置
        gatewayService.ifMeterTypeChange(meterTypeReadPO.getMeterTypeId());
    }
}
