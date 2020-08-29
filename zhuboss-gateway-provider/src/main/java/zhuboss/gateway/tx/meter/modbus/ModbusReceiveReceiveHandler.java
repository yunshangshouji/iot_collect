package zhuboss.gateway.tx.meter.modbus;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.adapter.TaskScheduler;
import zhuboss.gateway.adapter.bean.ReadInfo;
import zhuboss.gateway.mapper.LogWritePOMapper;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTUDownMeterMessage;
import zhuboss.gateway.po.LogWritePO;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.tx.channel.task.TaskReceiveHandler;
import zhuboss.gateway.tx.channel.task.modbus.ModbusReadTask;
import zhuboss.gateway.tx.channel.task.modbus.ModbusWriteTask;
import zhuboss.gateway.util.JavaUtil;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ModbusReceiveReceiveHandler implements TaskReceiveHandler<ModbusMessage> {
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    LogWritePOMapper logWritePOMapper;

    @Override
    public void handle(Channel channel, ModbusMessage message) {
        int funCode = message.getFunCode();
        int addr = message.getAdr();
        byte[] data = message.getData() ;
        String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);

        DeviceRequestMessage executingTask = ChannelKeys.readAttr(channel,ChannelKeys.EXECUTING_TASK);
        if(executingTask == null){
            //当前任务已过期或不存在
            return;
        }
        if(executingTask instanceof ModbusReadTask){
            ModbusReadTask modbusReadTask = (ModbusReadTask)executingTask;
            //读取任务，正常03,04,其它为错误码
            ReadInfo readInfo = modbusReadTask.getReadInfoList().get(modbusReadTask.getIdxReadInfo());
            if(readInfo.getCmd() != funCode){
                String errorMsg = String.format("返回%x和预期%x不一致,%s",funCode,readInfo.getCmd(), JavaUtil.bytesToHexString(data));
                log.error(errorMsg);
                ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,null);
                taskScheduler.addReadError(devNo, 1, null,null,null,addr, new Date(),errorMsg);
                return;
            }
            // 解析数据
            try {
                meterTypeFactory.parseRead(readInfo,data,modbusReadTask.getValues());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,null);
                String errorMsg = String.format("数据转换失败cmd:%x,%s",funCode,readInfo.getCmd(), JavaUtil.bytesToHexString(data));
                taskScheduler.addReadError(devNo, 1,null,null,null, addr, new Date(),errorMsg);
                return;
            }

            //任务结束 or 下一条任务
            if(modbusReadTask.getIdxReadInfo() == modbusReadTask.getReadInfoList().size() -1){
                //最后一条读取指令
                TaskScheduler taskScheduler = SpringContextUtils.getBean(TaskScheduler.class);
                taskScheduler.addRecord(devNo, 1, null,null,null,(long)addr, new Date(), new Date(),modbusReadTask.getValues(),null);
                ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,null);
                synchronized (executingTask){ //如果是手动任务，需要同步唤醒
                    executingTask.notifyAll();
                }
            }else{
                //下一条read指令
                modbusReadTask.setIdxReadInfo(modbusReadTask.getIdxReadInfo() + 1);
                ReadInfo nextReadInfo = modbusReadTask.getReadInfoList().get(modbusReadTask.getIdxReadInfo());
                ModbusMessage modbusMessage = new ModbusMessage((byte)addr,(byte)nextReadInfo.getCmd(),nextReadInfo.getReadCommand());
                channel.writeAndFlush(new DTUDownMeterMessage(modbusMessage.getEncodeBytes()));
            }

        }else if(executingTask instanceof ModbusWriteTask){
            ModbusWriteTask modbusWriteTask = (ModbusWriteTask)executingTask;
            if(modbusWriteTask.getCmd() != funCode){
                String errorMsg = String.format("返回%x和预期%x不一致,%s",funCode, modbusWriteTask.getCmd(), JavaUtil.bytesToHexString(data));
                log.error(errorMsg);
            }
            ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,null);
            //执行结果要保存到日志
            List<LogWritePO> logWritePOList = logWritePOMapper.selectByClause(new QueryClauseBuilder().andEqual(LogWritePO.Fields.TASK_UUID, modbusWriteTask.getTaskUUID()));
            if(logWritePOList.size() == 1){
                LogWritePO logWritePO = logWritePOList.get(0);
                logWritePO.setResCode(funCode);
                logWritePO.setRespTime(new Date());
                logWritePO.setResDataHex(JavaUtil.bytesToHexString(data));
                logWritePOMapper.updateByPK(logWritePO);
            }
            //通知http请求处理完成
            modbusWriteTask.setReceive(message);
            synchronized (modbusWriteTask){
                modbusWriteTask.notifyAll();
            }
        }
    }

}
