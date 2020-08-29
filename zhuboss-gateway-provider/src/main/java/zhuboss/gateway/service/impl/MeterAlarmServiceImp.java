package zhuboss.gateway.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.facade.vo.DataId;
import zhuboss.gateway.mapper.MeterAlarmDataPOMapper;
import zhuboss.gateway.mapper.MeterAlarmDevPOMapper;
import zhuboss.gateway.mapper.MeterAlarmPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterAlarmService;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.service.param.AddMeterAlarmParam;
import zhuboss.gateway.service.param.UpdateMeterAlarmParam;
import zhuboss.gateway.service.vo.DoAppCheckResult;
import zhuboss.gateway.util.MeterUtil;

import java.util.*;

@Service
public class MeterAlarmServiceImp implements MeterAlarmService {
    @Autowired
    MeterAlarmPOMapper meterAlarmPOMapper;
    @Autowired
    GatewayService gatewayService;
    @Autowired
    MeterAlarmDataPOMapper meterAlarmDataPOMapper;
    @Autowired
    MeterAlarmDevPOMapper meterAlarmDevPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    MeterTypeService meterTypeService;


    @Override
    public Map<Integer, ListMultimap<String,String>> map(Integer collectorId) {
        Map<Integer, ListMultimap<String,String>> result = new HashMap<>();
        List<MeterOverLimitVO> meterAlarmDevPOList = meterAlarmPOMapper.queryCollectorOverLimits(collectorId);
        Integer meterId = null;
        ListMultimap<String,String> overlimit = null;
        for(MeterOverLimitVO vo :meterAlarmDevPOList ){
            if(meterId == null || !meterId.equals(vo.getMeterId())){
                overlimit= ArrayListMultimap.create();
                result.put(vo.getMeterId(),overlimit);
                meterId = vo.getMeterId();
            }
            String text =  (vo.getFromValue() == null ? "":vo.getFromValue() )
                    + "~"
                    + ( vo.getToValue() == null ?"" : vo.getToValue());
            overlimit.put(vo.getTargetCode(),text);
        }
        return result;
    }

    @Override
    @Transactional
    public void addDevAlarm(Integer appId, Integer userId,AddMeterAlarmParam addDevAlarmParam) {
        MeterAlarmPO insert = new MeterAlarmPO();
        BeanMapper.copy(addDevAlarmParam,insert);
        insert.setAppId(appId);
        insert.setModifier(userId);
        insert.setModifyTime(new Date());
        meterAlarmPOMapper.insert(insert);

        //保存明细
        saveDetail(insert.getId(),addDevAlarmParam.getMeterId(),addDevAlarmParam.getMeterKindReadId());
        /**
         * 推送同步
         */
        List<Integer> collectorIdList = meterAlarmPOMapper.getCollectorId(insert.getId());
        if(collectorIdList != null){
            for(Integer collectorId : collectorIdList){
                gatewayService.ifCollectorChange(collectorId,null);
            }
        }
    }

    @Override
    @Transactional
    public void updateDevAlarm(Integer userId,UpdateMeterAlarmParam updateDevAlarmParam) {
        MeterAlarmPO update = meterAlarmPOMapper.selectByPK(updateDevAlarmParam.getId());
        BeanMapper.copy(updateDevAlarmParam,update);
        update.setModifier(userId);
        update.setModifyTime(new Date());
        meterAlarmPOMapper.updateByPK(update);
        //保存明细
        deleteDetail(updateDevAlarmParam.getId());
        saveDetail(updateDevAlarmParam.getId(),updateDevAlarmParam.getMeterId(),updateDevAlarmParam.getMeterKindReadId());

        /**
         * 推送同步
         */
        List<Integer> collectorIdList = meterAlarmPOMapper.getCollectorId(update.getId());
        if(collectorIdList != null){
            for(Integer collectorId : collectorIdList){
                gatewayService.ifCollectorChange(collectorId,null);
            }
        }
    }

    @Override
    @Transactional
    public void deleteDevAlarm(Long id) {
        //删除明细
        deleteDetail(id);
        meterAlarmPOMapper.deleteByPK(id);
        /**
         * 推送同步
         */
        List<Integer> collectorIdList = meterAlarmPOMapper.getCollectorId(id);
        if(collectorIdList != null){
            for(Integer collectorId : collectorIdList){
                gatewayService.ifCollectorChange(collectorId,null);
            }
        }
    }

    private void deleteDetail(Long id){
        meterAlarmDevPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterAlarmDevPO.Fields.METER_ALARM_ID,id));
        meterAlarmDataPOMapper.deleteByClause(new QueryClauseBuilder().andEqual(MeterAlarmDataPO.Fields.METER_ALARM_ID,id));
    }
    private void saveDetail(Long devAlarmId,Integer[] devIds,Integer[] meterKindReadIds){
        for(Integer devId : devIds){
            MeterAlarmDevPO devAlarmDevPO = new MeterAlarmDevPO();
            devAlarmDevPO.setMeterAlarmId(devAlarmId);
            devAlarmDevPO.setMeterId(devId);
            devAlarmDevPO.setCreateTime(new Date());
            meterAlarmDevPOMapper.insert(devAlarmDevPO);
        }
        for(Integer meterKindReadId : meterKindReadIds){
            MeterAlarmDataPO devAlarmDataPO = new MeterAlarmDataPO();
            devAlarmDataPO.setMeterAlarmId(devAlarmId);
            devAlarmDataPO.setMeterKindReadId(meterKindReadId);
            devAlarmDataPO.setCreateTime(new Date());
            meterAlarmDataPOMapper.insert(devAlarmDataPO);
        }
    }

    @Override
    public GridTable<MeterAlarmPOExt> query(QueryClauseBuilder queryClauseBuilder) {
        List<MeterAlarmPO> list = meterAlarmPOMapper.selectByClause(queryClauseBuilder);
        List<MeterAlarmPOExt> results = BeanMapper.mapList(list,MeterAlarmPOExt.class);
        Map<Integer,List<DataId> > map = new HashMap<>();
        for(MeterAlarmPOExt devAlarmPOExt : results){
            //引用的设备列表
            List<Integer> meterIdList = new ArrayList<>();
            List<String> meterNameList = new ArrayList<>();
            List<MeterAlarmDevPO> devAlarmDevPOList = meterAlarmDevPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterAlarmDevPO.Fields.METER_ALARM_ID,devAlarmPOExt.getId()));
            for(MeterAlarmDevPO devAlarmDevPO : devAlarmDevPOList){
                MeterPO meterPO = meterPOMapper.selectByPK(devAlarmDevPO.getMeterId());
                String targetName = MeterUtil.getMeterName(meterPO);
                meterIdList.add(meterPO.getId());
                meterNameList.add(targetName);
            }
            devAlarmPOExt.setMeterId(meterIdList);
            devAlarmPOExt.setMeterNames(Strings.join(meterNameList.iterator(),','));
            //引用的数据项列表
            List<DataId> itemList = map.get(devAlarmPOExt.getMeterKindId());
            if(itemList == null){
                itemList = meterTypeService.queryMeterKindVar(devAlarmPOExt.getMeterKindId());
                map.put(devAlarmPOExt.getMeterKindId(),itemList);
            }
            /**
             * 设置展示的名称
             */
            List<Integer> meterKindReadIdList = new ArrayList<>();
            List<String> targetCodeList = new ArrayList<>();
            List<String> targetNameList = new ArrayList<>();
            List<MeterAlarmDataPO> devAlarmDataPOList = meterAlarmDataPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterAlarmDataPO.Fields.METER_ALARM_ID,devAlarmPOExt.getId()));
            for(MeterAlarmDataPO devAlarmDataPO : devAlarmDataPOList){
                String targetCode="",targetName="";
                Integer meterKindReadId = null;
                for(DataId item : itemList){
                    if(item.getId().equals(devAlarmDataPO.getMeterKindReadId())){
                        meterKindReadId = item.getId();
                        targetCode = item.getValue();
                        targetName = item.getText();
                        break;
                    }
                }
                meterKindReadIdList.add(meterKindReadId);
                targetCodeList.add(targetCode);
                targetNameList.add(targetName);
            }
            devAlarmPOExt.setMeterKindReadId(meterKindReadIdList);
            devAlarmPOExt.setTargetCodes(Strings.join(targetCodeList.iterator(),','));
            devAlarmPOExt.setTargetNames(Strings.join(targetNameList.iterator(),','));
        }
        Integer cnt = meterAlarmPOMapper.selectCountByClause(queryClauseBuilder);
        return new GridTable<MeterAlarmPOExt>(results,cnt);
    }

    @Override
    public void doCheck(Integer userId, Long devAlarmId) {
        MeterAlarmPO devAlarmPO = meterAlarmPOMapper.selectByPK(devAlarmId);
        if (devAlarmPO.getCheckFlag() == 1) {
            throw new BussinessException("当前是已确认状态，不能再确认！");
        }
        if(devAlarmPO.getAlarmFlag() == 1){
            throw new BussinessException("报警未解除状态下，不允许确认！");
        }
        devAlarmPO.setCheckFlag(1);
        devAlarmPO.setLastCheckTime(new Date());
        devAlarmPO.setLastCheckUserId(userId);
        meterAlarmPOMapper.updateByPK(devAlarmPO);
    }

    @Override
    public DoAppCheckResult doStationCheck(Integer userId, Integer stationId) {
        GridTable<MeterAlarmPOExt> gridTable = this.query(new QueryClauseBuilder()
                .andEqual("station_id",stationId)
                .andEqual("check_flag",0) //未确认
                .andSQL(" not EXISTS (SELECT 1 FROM alarm_over_limit WHERE dev_alarm_id = dev_alarm.id)")
//                .andEqual("alarm_flag",0) //未越限
        );
        DoAppCheckResult result = new DoAppCheckResult();
        for(MeterAlarmPOExt devAlarmPO : gridTable.getRows()){
            this.doCheck(userId,devAlarmPO.getId());
            result.setSize(result.getSize()+1);
            result.getTargetNames().add(devAlarmPO.getMeterNames()+":"+devAlarmPO.getTargetNames());
        }
        return  result;
    }

    @Override
    public MeterAlarmPO getMeterAlarmPoByRefId(Integer appId, String refId) {
        return meterAlarmPOMapper.selectOneByClause(new QueryClauseBuilder()
                .andEqual(MeterAlarmPO.Fields.APP_ID,appId)
                .andEqual(MeterAlarmPO.Fields.REF_ID,refId)
        );
    }

}
