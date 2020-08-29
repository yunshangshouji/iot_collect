package zhuboss.gateway.tx.gateway.smart.provider.zhuboss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.adapter.TaskScheduler;
import zhuboss.gateway.common.SysErrorType;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.mapper.*;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.service.SysErrorLogService;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.channel.task.modbus.ModbusReadTask;
import zhuboss.gateway.tx.channel.task.modbus.ModbusWriteTask;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo.ZhubossRegisterDown;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.upvo.*;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.tx.meter.modbus.ModbusMessage;
import zhuboss.gateway.util.JavaUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ZhubossDispatcher implements IDispatcher{
    @Autowired
    GatewayService gatewayService;
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    MeterAlarmPOMapper meterAlarmPOMapper;
    @Autowired
    SysErrorLogService sysErrorLogService;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    TxCollectorPOMapper txCollectorPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    CollectorService collectorService;
    @Autowired
    MeterService meterService;
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;

    @Override
    public void dispatch(Channel channel, ZhubossDataPackage zhubossDataPackage) throws Exception {
        if(zhubossDataPackage.getType() == ZhubossPackageType.HEART_BEAT){
            //必须下发心跳包，否则网关认为连接已死！
            channel.writeAndFlush(new ZhubossDataPackage(ZhubossPackageType.HEART_BEAT,new byte[]{})).sync();
        }else if(zhubossDataPackage.getType() == ZhubossPackageType.REGISTER){
            String json = new String(zhubossDataPackage.getData());
            ZhubossRegisterParam zhubossRegisterParam = JSON.parseObject(json, ZhubossRegisterParam.class);
            register(channel, zhubossRegisterParam);

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.DOWN_MEA){
            //TODO 客户端可能返回配置解析错误
            // 连接状态置为error,并可查看客户端发回来的error
            log.error("下发配置应答：" + new String(zhubossDataPackage.getData())) ;

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.DATA_REPORT){
            byte[] oriBytes = JavaUtil.decompress(zhubossDataPackage.getData());
            cronReport(channel,new String(oriBytes));

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.DEV_OFFLINE){ //仪表离线
            meterOffline(channel,new String(zhubossDataPackage.getData()));

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.DEV_ONLINE){ //仪表上线
            meterOnline(channel,new String(zhubossDataPackage.getData()));

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.OVER_LIMIT_ON){
            overlimit(channel, new String(zhubossDataPackage.getData()));

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.SIGNAL){
            onSignal(channel,new String(zhubossDataPackage.getData()));

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.WRITE){
            String json = new String(zhubossDataPackage.getData());
            log.error(json);
            ModbusWriteTask modbusWriteTask = (ModbusWriteTask)ChannelKeys.readAttr(channel,ChannelKeys.EXECUTING_TASK);
            if(modbusWriteTask != null){
                byte[] data = Base64Utils.decodeFromString(json);
                modbusWriteTask.setReceive(new ModbusMessage(data[0],data[1],data));
                synchronized (modbusWriteTask){
                    modbusWriteTask.notifyAll();
                }
            }
        }else if(zhubossDataPackage.getType() == ZhubossPackageType.READ){
            String json = new String(zhubossDataPackage.getData());
            String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
            ZhubossCronReport zhubossCronReport = JSON.parseObject(json,ZhubossCronReport.class);
            taskScheduler.addRecord(devNo, zhubossCronReport.getCom(),zhubossCronReport.getLoraAddr(),zhubossCronReport.getIp()
                    ,zhubossCronReport.getPort(), (long) zhubossCronReport.getAddr(), new Date(),zhubossCronReport.getReadTimeDate(), zhubossCronReport.getValues(), zhubossCronReport.getErrorMsg() );
            ModbusReadTask modbusReadTask = (ModbusReadTask)ChannelKeys.readAttr(channel,ChannelKeys.EXECUTING_TASK);
            if(modbusReadTask != null){
                modbusReadTask.setValues(JSON.parseObject(json));
                synchronized (modbusReadTask){
                    modbusReadTask.notifyAll();
                }
            }

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.ERROR){
            String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
            String text = new String(zhubossDataPackage.getData());
            Integer collectorId = collectorService.getCachedCollector(devNo).getCollector().getId();
            Map<String,Object> errorInfo = new HashMap<>();
            errorInfo.put(TxCollectorPO.Fields.COLLECTOR_ID.name(),collectorId);
            errorInfo.put(TxCollectorPO.Fields.DEV_ERROR_TIME.name(),new Date());
            errorInfo.put(TxCollectorPO.Fields.DEV_ERROR_MSG.name(),text);
            txCollectorPOMapper.insertOrUpdate(errorInfo);

        }else if(zhubossDataPackage.getType() == ZhubossPackageType.RETRIEVE_STS){
            String json = new String(zhubossDataPackage.getData());
            ChannelKeys.setAttr(channel,ChannelKeys.RETRIEVE_STS,json);
            synchronized (channel){
                channel.notifyAll();
            }
        }

        //记录网关活动时间，用于离线判断
        String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
        if(StringUtils.hasText(devNo)){
            collectorService.triggerActive(devNo);
        }
    }



    public void register(Channel channel, ZhubossRegisterParam zhubossRegisterParam) throws InterruptedException {
        String devNo = zhubossRegisterParam.getDevNo();
        log.info("网关注册: {}", devNo);
        Date onlineTime = new Date();
        JsonResponse operateResult = gatewayService.checkRegisterResult(zhubossRegisterParam);
        ZhubossRegisterDown checkRegisterResult = new ZhubossRegisterDown(operateResult.getResult()? ZhubossRegisterDown.success: ZhubossRegisterDown.fail,System.currentTimeMillis(),operateResult.getMsg());
        if(operateResult.getResult() == false){
            channel.writeAndFlush(new ZhubossDataPackage(ZhubossPackageType.REGISTER,checkRegisterResult)).sync();
            channel.close();
        }else{
            //如果已经存在注册，则要断开连接
            Channel existsChanel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
            if(existsChanel != null){
                existsChanel.close();
            }
            //注册应答
            Integer appId = collectorService.getCachedCollector(devNo).getCollector().getAppId();
            ChannelKeys.registerGatewayId(channel, devNo, CollectorTypeEnum.ZHUBOSS,appId);
            ZhubossDataPackage registerAck = new ZhubossDataPackage(ZhubossPackageType.REGISTER,checkRegisterResult);
            channel.writeAndFlush(registerAck);
            log.info("注册应答：{}",registerAck);

            //时间戳下发
            ZhubossDataPackage timestamp = new ZhubossDataPackage(ZhubossPackageType.TIME_STAMP,System.currentTimeMillis()+"");
            channel.writeAndFlush(timestamp).sync();

            //下发配置文件
            gatewayService.doDownMeta(devNo);

            //更新上线时间
            Integer collectorId = collectorService.getCachedCollector(devNo).getCollector().getId();
            Map<String,Object> map = new HashMap<>();
            map.put(TxCollectorPO.Fields.COLLECTOR_ID.name(),collectorId);
            map.put(TxCollectorPO.Fields.DEV_VERSION.name(),zhubossRegisterParam.getDevVer());
            map.put(TxCollectorPO.Fields.APP_VERSION.name(),zhubossRegisterParam.getAppVer());
            map.put(TxCollectorPO.Fields.IF_NAME.name(),zhubossRegisterParam.getIfName());
            //客户端首次登录没有时间，等服务器下发，因此上线时间为服务器当前时间
            map.put(TxCollectorPO.Fields.APP_START_TIME.name(),
                    (zhubossRegisterParam.getAppStartTime()==null || zhubossRegisterParam.getAppStartTime()==0)
                    ?new Date() : new Date(zhubossRegisterParam.getAppStartTime()*1000l));
            map.put(TxCollectorPO.Fields.LAST_ONLINE_TIME.name(),new Date());
            txCollectorPOMapper.insertOrUpdate(map);

        }

    }

    private void cronReport(Channel channel, String json) throws InterruptedException {
        String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
        log.debug("{}定时上报:"+json,devNo);
        List<ZhubossCronReport> zhubossCronReportList = JSON.parseArray(json, ZhubossCronReport.class);
        if(zhubossCronReportList.size() ==0 ){
            return;
        }

        Date reportTime = new Date();
        Date firstTime=zhubossCronReportList.get(0).getReadTimeDate(), lastTime = zhubossCronReportList.get(0).getReadTimeDate();
        for(ZhubossCronReport zhubossCronReport : zhubossCronReportList){
            taskScheduler.addRecord(devNo, zhubossCronReport.getCom(),zhubossCronReport.getLoraAddr(),zhubossCronReport.getIp(),zhubossCronReport.getPort(), zhubossCronReport.getAddr(), reportTime, zhubossCronReport.getReadTimeDate(),zhubossCronReport.getValues(), zhubossCronReport.getErrorMsg() );

            if(zhubossCronReport.getReadTimeDate().compareTo(firstTime)<0){
                firstTime = zhubossCronReport.getReadTimeDate();
            }
            if(zhubossCronReport.getReadTimeDate().compareTo(lastTime)>0){
                lastTime = zhubossCronReport.getReadTimeDate();
            }
        }
        Integer collectorId = collectorService.getCachedCollector(devNo).getCollector().getId();
        Map<String,Object> map = new HashMap<>();
        map.put(TxCollectorPO.Fields.COLLECTOR_ID.name(),collectorId);
        map.put(TxCollectorPO.Fields.LAST_REPORT_TIME.name(),new Date());
        map.put(TxCollectorPO.Fields.LAST_REPORT_COUNT.name(),zhubossCronReportList.size());
        map.put(TxCollectorPO.Fields.LAST_LOOP_SECONDS.name(), (int)((lastTime.getTime() - firstTime.getTime())/1000));
        txCollectorPOMapper.insertOrUpdate(map);
    }

    private void meterOffline(Channel channel,String json){
        log.debug("仪表离线:"+json);
        String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
        try{
            ZhubossMeterOffline zhubossMeterOffline = JSON.parseObject(json, ZhubossMeterOffline.class);
            taskScheduler.receiveMeterOfflineMessage(devNo,zhubossMeterOffline);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            sysErrorLogService.log(SysErrorType.zhuboss_DEV_OFFLINE,e.getMessage(),e);
        }
    }

    private void meterOnline(Channel channel,String json){
        log.debug("仪表上线"+json);
        String collectorNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
        try{
            ZhubossMeterOnline zhubossMeterOffline = JSON.parseObject(json, ZhubossMeterOnline.class);
            taskScheduler.receiveMeterOnlineMessage(collectorNo,zhubossMeterOffline);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            sysErrorLogService.log(SysErrorType.zhuboss_DEV_OFFLINE,e.getMessage(),e);
        }
    }

    private void overlimit(Channel channel,String json){
        try {
            log.debug(json);
            String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
            ZhubossOverLimit zhubossOverLimit = JSON.parseObject(json, ZhubossOverLimit.class);

            CollectorPO collectorPO = collectorService.getCollectorPO(devNo);
            MeterPO meterPO = meterService.select(collectorPO.getId(),zhubossOverLimit.getComPort(),zhubossOverLimit.getLoraAddr(),zhubossOverLimit.getIp(),zhubossOverLimit.getPort(),zhubossOverLimit.getAddr());
            if(meterPO == null){
                return;
            }
            for(Map.Entry<String,Object> entry : zhubossOverLimit.getValues().entrySet()){
                String targetCode = entry.getKey();
                BigDecimal value = TypeUtils.castToBigDecimal(entry.getValue());
                MeterKindReadPO meterKindReadPO = meterKindReadPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(MeterKindReadPO.Fields.TARGET_CODE,targetCode).andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterPO.getMeterKindId()));
                List<MeterAlarmPO> meterAlarmPOList = meterAlarmPOMapper.selectByClause(new QueryClauseBuilder().andSQL("EXISTS(SELECT 1 FROM meter_alarm_dev WHERE meter_alarm_id = meter_alarm.`id` AND meter_id = "+meterPO.getId()+") " +
                        "AND EXISTS(SELECT 1 FROM meter_alarm_data WHERE meter_alarm_id = meter_alarm.`id` AND meter_kind_read_id = "+meterKindReadPO.getId()+")"));
                MeterAlarmPO meterAlarmPO = null;
                for(MeterAlarmPO item : meterAlarmPOList){
                    if ((item.getFromValue() ==null || value.compareTo(item.getFromValue())>=0)
                            &&(item.getToValue() == null || value.compareTo(item.getToValue())<=0)){
                        meterAlarmPO = item;
                        break;
                    }
                }
                if(meterAlarmPO == null){
                    log.error("未找到超限匹配项,{}:{}",targetCode,value);
                    continue;
                }

                taskScheduler.addAlarm(devNo, zhubossOverLimit.getComPort(),zhubossOverLimit.getLoraAddr(), zhubossOverLimit.getIp(), zhubossOverLimit.getPort(),zhubossOverLimit.getAddr(),
                        zhubossOverLimit.getValues(),
                        meterAlarmPO,
                        targetCode,
                        meterKindReadPO.getTargetName(),
                        value
                );
            }

        }catch (Exception e){
            log.error(e.getMessage(),e);
            sysErrorLogService.log(SysErrorType.zhuboss_OVER_LIMIT,e.getMessage(),e);
        }

    }

    private void onSignal(Channel channel,String json){
        log.debug("遥信:"+json);
        String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
        CollectorPO collectorPO = collectorService.getCollectorPO(devNo);
        try {
            ZhubossOnSignal zhubossOnSignal = JSON.parseObject(json, ZhubossOnSignal.class);
            taskScheduler.sendSignalMQ(collectorPO.getId(),zhubossOnSignal);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            sysErrorLogService.log(SysErrorType.zhuboss_OVER_LIMIT,e.getMessage(),e);
        }

    }

}
