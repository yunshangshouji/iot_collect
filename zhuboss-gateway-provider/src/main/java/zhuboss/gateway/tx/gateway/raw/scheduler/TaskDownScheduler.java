package zhuboss.gateway.tx.gateway.raw.scheduler;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.adapter.TaskScheduler;
import zhuboss.gateway.adapter.bean.Dlt645Var;
import zhuboss.gateway.adapter.bean.ReadInfo;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.tx.channel.task.AbstractTask;
import zhuboss.gateway.tx.channel.task.DebugTask;
import zhuboss.gateway.tx.channel.task.MyStack;
import zhuboss.gateway.tx.channel.task.dlt645.Dlt645ReadTask;
import zhuboss.gateway.tx.channel.task.modbus.ModbusReadTask;
import zhuboss.gateway.tx.channel.task.modbus.ModbusWriteTask;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTUDownMeterMessage;
import zhuboss.gateway.tx.meter.dlt645.Dlt645Message;
import zhuboss.gateway.tx.meter.modbus.ModbusMessage;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

@Slf4j
@Component
public class TaskDownScheduler {
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    TaskScheduler taskScheduler;

    @Scheduled(cron = "*/1 * * * * ?")
    public void run() {
        try {
            Iterator<Channel> iterator = MyChannelGroup.allChannels.iterator();
            while (iterator.hasNext()) {
                try {
                    Channel channel = iterator.next();
                    CollectorTypeEnum collectorTypeEnum = ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_TYPE);
                    //只有透传的网关才平台定时采集
                    if(collectorTypeEnum!=null && collectorTypeEnum.name().startsWith("RAW_")){
                        run(channel);
                    }

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
        }catch (Exception e){
            log.error("job失败",e);
        }
    }

    public boolean run(Channel channel) throws IOException {
        String devNo = ChannelKeys.readAttr(channel, ChannelKeys.COLLECTOR_NO);
        if (devNo == null) return true;

        MyStack<ModbusReadTask> taskStack = ChannelKeys.readAttr(channel, ChannelKeys.REQUEST_STACK);
        if (taskStack == null || taskStack.queue.size() == 0) return true;

        AbstractTask abstractTask = (AbstractTask)ChannelKeys.readAttr(channel, ChannelKeys.EXECUTING_TASK);


        //当前已经存在任务且未过期
        if(abstractTask != null && abstractTask.getExpireTime()!=null && abstractTask.getExpireTime() > System.currentTimeMillis()){
            return true;
        }

        if(abstractTask != null && abstractTask instanceof ModbusReadTask){ //任务过期
            ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,null);
            taskScheduler.addReadError(devNo,1,null,null,null,((ModbusReadTask)abstractTask).getAddr(),new Date(),"不在线");
        }


        //执行一一次的普通读取任务
        AbstractTask newTask = taskStack.pop();
        if (newTask == null) return true;
        log.debug("size{},采集 {}.{}", taskStack.queue.size(),devNo, newTask.getHashAddr());
        ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK, newTask);
        if(newTask instanceof ModbusReadTask){
            ModbusReadTask modbusReadTask = (ModbusReadTask)newTask;
            newTask.setExpireTime(System.currentTimeMillis() + 6*1000); //6秒钟过期
            //发送第一条read指令，后续指令会等到应答后立即下发
            ReadInfo readInfo = modbusReadTask.getReadInfoList().get(0);
            ModbusMessage modbusMessage = new ModbusMessage((byte) modbusReadTask.getAddr(),(byte)readInfo.getCmd(),readInfo.getReadCommand());
            channel.writeAndFlush(new DTUDownMeterMessage(modbusMessage.getEncodeBytes()));
        }else if(newTask instanceof ModbusWriteTask){
            ModbusWriteTask modbusWriteTask = (ModbusWriteTask)newTask;
            modbusWriteTask.setExpireTime(System.currentTimeMillis() + 6*1000); //6秒钟过期
            ModbusMessage modbusMessage = new ModbusMessage((byte) modbusWriteTask.getAddr(),(byte) modbusWriteTask.getCmd(), modbusWriteTask.getWriteBytes());
            channel.writeAndFlush(new DTUDownMeterMessage(modbusMessage.getEncodeBytes()));
        }else if(newTask instanceof DebugTask){
            DebugTask debugTask = (DebugTask)newTask;
            debugTask.setExpireTime(System.currentTimeMillis() + 6*1000); //6秒钟过期
            channel.writeAndFlush(new DTUDownMeterMessage(debugTask.getRquestData()));
        }else if(newTask instanceof Dlt645ReadTask){
            Dlt645ReadTask dlt645ReadTask = (Dlt645ReadTask)newTask;
            newTask.setExpireTime(System.currentTimeMillis() + 6*1000); //6秒钟过期
            //TODO DLT协议需要多次交互
            Dlt645Var dlt645Var = dlt645ReadTask.getDlt645VarList().get(0);
            Dlt645Message dlt645Message = new Dlt645Message(dlt645ReadTask.getProtocolEnum(),dlt645ReadTask.getAddr(),dlt645Var.getCode());
            channel.writeAndFlush(new DTUDownMeterMessage(dlt645Message.getEncodeBytes()));
        }
        return false;
    }
}
