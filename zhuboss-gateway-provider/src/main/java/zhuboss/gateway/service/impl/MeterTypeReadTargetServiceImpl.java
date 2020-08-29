package zhuboss.gateway.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.mapper.MeterTypePOMapper;
import zhuboss.gateway.mapper.MeterTypeReadPOMapper;
import zhuboss.gateway.mapper.MeterTypeReadTargetPOMapper;
import zhuboss.gateway.po.MeterTypeReadPO;
import zhuboss.gateway.po.MeterTypeReadTargetPO;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterTypeReadTargetService;
import zhuboss.gateway.service.vo.AddMeterTypeReadTargetParam;
import zhuboss.gateway.service.vo.UpdateMeterTypeReadTargetParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.spring.cache.CacheConstants;

import java.util.Date;
import java.util.List;

@Service
public class MeterTypeReadTargetServiceImpl implements MeterTypeReadTargetService {
    @Autowired
    MeterTypeReadTargetPOMapper meterTypeReadTargetPOMapper;
    @Autowired
    MeterTypePOMapper meterTypePOMapper;
    @Autowired
    MeterTypeReadPOMapper meterTypeReadPOMapper;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    GatewayService gatewayService;

    @Override
    @Transactional
    public void addMeterTypeTarget(AddMeterTypeReadTargetParam addMeterTypeTargetParam) {
        MeterTypeReadTargetPO insert = new MeterTypeReadTargetPO();
        BeanMapper.copy(addMeterTypeTargetParam,insert);
        insert.setCreateTime(new Date());
        insert.setAddr(Integer.parseInt(addMeterTypeTargetParam.getAddrHex(),16));
        MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(addMeterTypeTargetParam.getReadId());
        insert.setMeterTypeId(meterTypeReadPO.getMeterTypeId());
        insert.setAppId(meterTypeReadPO.getAppId());
        if(meterTypeReadPO.getCmd() == 0x01 || meterTypeReadPO.getCmd() == 0x02){
            insert.setValueType("BIT");
        }
        meterTypeReadTargetPOMapper.insert(insert);
        //重新计算地址
        reCalculateStartEndAddr(insert.getReadId());
        //向网关下载配置
        gatewayService.ifMeterTypeChange(meterTypeReadPO.getMeterTypeId());
    }

    @Override
    @Transactional
    public void updateMeterTypeTarget(UpdateMeterTypeReadTargetParam updateMeterTypeTargetParam) {
        MeterTypeReadTargetPO update = meterTypeReadTargetPOMapper.selectByPK(updateMeterTypeTargetParam.getId());
        BeanMapper.copy(updateMeterTypeTargetParam,update);
        update.setModifyTime(new Date());
        update.setAddr(Integer.parseInt(updateMeterTypeTargetParam.getAddrHex(),16));
        meterTypeReadTargetPOMapper.updateByPK(update);
        //重新计算地址
        reCalculateStartEndAddr(update.getReadId());
        //向网关下载配置
        gatewayService.ifMeterTypeChange(update.getMeterTypeId());
    }

    @Override
    @Transactional
    public void deleteMeterTypeTargetId(Integer meterTypeTargetId) {
        //删除记录
        MeterTypeReadTargetPO meterTypeReadTargetPO = meterTypeReadTargetPOMapper.selectByPK(meterTypeTargetId);
        meterTypeReadTargetPOMapper.deleteByPK(meterTypeTargetId);
        //重新计算地址
        reCalculateStartEndAddr(meterTypeReadTargetPO.getReadId());
        //向网关下载配置
        gatewayService.ifMeterTypeChange(meterTypeReadTargetPO.getMeterTypeId());
    }

    private void reCalculateStartEndAddr(Integer meterTypeReadId){
        MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(meterTypeReadId);

        List<MeterTypeReadTargetPO> meterTypeReadTargetPOList = meterTypeReadTargetPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterTypeReadTargetPO.Fields.READ_ID,meterTypeReadId)
                .sort(MeterTypeReadTargetPO.Fields.ADDR)
        );
        if(meterTypeReadTargetPOList.size() == 0 ){
            meterTypeReadPO.setStartAddr(null);
            meterTypeReadPO.setLen(null);
            meterTypeReadPO.setEndAddr(null);
            return;
        }else if(meterTypeReadPO.getCmd()==3 || meterTypeReadPO.getCmd() == 4 ){
            int minAddr = meterTypeReadTargetPOList.get(0).getAddr();
            int maxAddr = meterTypeReadTargetPOList.get(meterTypeReadTargetPOList.size()-1).getAddr();
            String maxFunc = meterTypeReadTargetPOList.get(meterTypeReadTargetPOList.size()-1).getValueType();
            if(maxFunc.contains("32") || maxFunc.equalsIgnoreCase("IEEE754")){ //2个字
                maxAddr ++;
            }
            if(meterTypeReadPO.getCmd() == 1 || meterTypeReadPO.getCmd()==2){
                meterTypeReadPO.setLen(maxAddr);
                meterTypeReadPO.setEndAddr(null);
            }else if (meterTypeReadPO.getCmd() == 3 || meterTypeReadPO.getCmd()==4){
                meterTypeReadPO.setStartAddr(minAddr);
                meterTypeReadPO.setLen(maxAddr-minAddr+1);
                meterTypeReadPO.setEndAddr(maxAddr);
                if(meterTypeReadPO.getLen()> 128){
                    throw new BussinessException("Modbus最大读取长度128字(256字节),当前长度"+meterTypeReadPO.getLen());
                }
            }
        }else if(meterTypeReadPO.getCmd()==1 || meterTypeReadPO.getCmd() == 2 ){ //位操作
            int minAddr = meterTypeReadTargetPOList.get(0).getAddr();
            int maxAddr = meterTypeReadTargetPOList.get(meterTypeReadTargetPOList.size()-1).getAddr();
            meterTypeReadPO.setStartAddr(minAddr);
            meterTypeReadPO.setLen(maxAddr - minAddr + 1);
            meterTypeReadPO.setEndAddr(maxAddr); // bit不适用此字段
        }
        //保存结果
        meterTypeReadPOMapper.updateByPK(meterTypeReadPO);
    }
}
