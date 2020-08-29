package zhuboss.gateway.tx.gateway.raw.scheduler;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.adapter.bean.Dlt6452007MeterType;
import zhuboss.gateway.adapter.bean.Dlt645MeterType;
import zhuboss.gateway.adapter.bean.MeterType;
import zhuboss.gateway.adapter.bean.ModbusMeterType;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.tx.channel.task.MyStack;
import zhuboss.gateway.tx.channel.task.dlt645.Dlt645ReadTask;
import zhuboss.gateway.tx.channel.task.modbus.ModbusReadTask;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.MeterTypeReadPOMapper;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.AppCycleService;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.vo.CachedCollector;

import java.util.Iterator;

@Component
@Slf4j
public class TaskCreateScheduler {

    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    AppCycleService appCycleService;
    @Autowired
    MeterPOMapper meterPOMapper;

    @Autowired
    MeterTypeReadPOMapper meterTypeReadPOMapper;

    @Autowired
    CollectorService collectorService;

    /**
     * 每5秒钟定时器会触发
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void run() {
        {
            long ts = System.currentTimeMillis() / 1000L;
            ts = ts - (ts % 5); //精度同最小调度周期5秒
            //目标需要定时执行的，5秒、10、15、

            int dtuCount = 0;
            Iterator<Channel> iterator = MyChannelGroup.allChannels.iterator();
            while (iterator.hasNext()) {
                try {
                    Channel channel = iterator.next();
                    String devNo = ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_NO);
                    if(devNo != null){
                        dtuCount ++ ;
                    }
                    CollectorTypeEnum collectorTypeEnum = ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_TYPE);
                    if(!(devNo != null && collectorTypeEnum != null  && CollectorTypeEnum.isRAW(collectorTypeEnum))){
                        continue;
                    }
                    runChannel(channel, devNo,ts);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
            }
            log.info("BatchTaskJob,在线DTU数量:{}", dtuCount);

        }
    }

    public void runChannel(Channel channel, String devNo,long ts) {
        CachedCollector  cachedCollector = collectorService.getCachedCollector(devNo);
        if(cachedCollector == null){
            log.error("DTU {} 不存在",devNo);
            return;
        }
        //TODO 支持透传的网关

        //只有采集器类型1、2才执行定时采集; 顺舟采集器的透传也需要
        MyStack<DeviceRequestMessage> taskStack = ChannelKeys.readAttr(channel, ChannelKeys.REQUEST_STACK);
        if (taskStack == null) {
            taskStack = new MyStack<DeviceRequestMessage>();
            ChannelKeys.setAttr(channel, ChannelKeys.REQUEST_STACK, taskStack);
        }
        for(MeterPO meterPO : cachedCollector.getMeterPOList()) {
            MeterType meterType = meterTypeFactory.load(meterPO.getMeterTypeId());
            if(meterType instanceof ModbusMeterType && ((ModbusMeterType)meterType).getReadInfos().size() == 0){
                //新建的表尚未定义读取指令
                continue;
                /**
                 * 轮询到点判断
                 */
            }
            Integer seconds = appCycleService.getRawCycleSeconds(cachedCollector.getCollector(),cachedCollector.getCollector().getAppId(),
                    cachedCollector.getCollector().getStationId(), //缓存key不能为null
                    meterPO.getMeterKindId(), meterPO.getMeterTypeId());
            if (ts % seconds > 0) {
                continue;
            }
            /**
             * 生成任务存入task 队列
             */
            if(meterType instanceof ModbusMeterType){
                ModbusReadTask modbusReadTask = new ModbusReadTask(meterPO.getAddr().intValue(), ((ModbusMeterType)meterType).getReadInfos());
                if (!taskStack.contains(modbusReadTask)) {
                    taskStack.push(modbusReadTask);
                }
            }else if(meterType instanceof Dlt645MeterType){
                ProtocolEnum protocolEnum = meterType instanceof Dlt6452007MeterType ? ProtocolEnum.DLT2007 : ProtocolEnum.DLT1997;
                Dlt645ReadTask dlt645ReadTask = new Dlt645ReadTask(protocolEnum,meterPO.getAddr(),((Dlt645MeterType)meterType).getDlt645VarList());
                if (!taskStack.contains(dlt645ReadTask)) {
                    taskStack.push(dlt645ReadTask);
                }
            }else {
                log.error("Un support" + meterType);
            }

        }
        ChannelKeys.setAttr(channel, ChannelKeys.REQUEST_STACK, taskStack);
    }

}
