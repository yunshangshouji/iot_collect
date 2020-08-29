package zhuboss.gateway.tx.meter.dlt645;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.adapter.TaskScheduler;
import zhuboss.gateway.adapter.bean.Dlt645Var;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.tx.gateway.raw.provider.hongdian.message.DTUDownMeterMessage;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.tx.channel.task.TaskReceiveHandler;
import zhuboss.gateway.tx.channel.task.dlt645.Dlt645ReadTask;

import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class Dlt645ReceiveHandler implements TaskReceiveHandler<Dlt645Message> {
    @Autowired
    TaskScheduler taskScheduler;

    @Override
    public void handle(Channel channel, Dlt645Message message) throws IOException {
        String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);

        DeviceRequestMessage executingTask = ChannelKeys.readAttr(channel,ChannelKeys.EXECUTING_TASK);
        if(executingTask == null){
            //当前任务已过期或不存在
            return;
        }
        if(executingTask instanceof Dlt645ReadTask){
            Dlt645ReadTask dlt645ReadTask = (Dlt645ReadTask)executingTask;

            // 解析数据
            try {
                if(message.isOk()){
                    dlt645ReadTask.getValues().put(dlt645ReadTask.getDlt645VarList().get(dlt645ReadTask.getIdxReadInfo()).getName(),message.getValue());
                }else{
                    log.error("dataId:{}读取失败.", dlt645ReadTask.getDlt645VarList().get(dlt645ReadTask.getIdxReadInfo()).getName());
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,null);
                taskScheduler.addReadError(devNo, 1,null,null,null, dlt645ReadTask.getAddr(), new Date(),"数据转换失败");
                return;
            }

            //任务结束 or 下一条任务
            if(dlt645ReadTask.getIdxReadInfo() == dlt645ReadTask.getDlt645VarList().size() -1){
                //最后一条读取指令
                TaskScheduler taskScheduler = SpringContextUtils.getBean(TaskScheduler.class);
                taskScheduler.addRecord(devNo, 1, null,null,null,dlt645ReadTask.getAddr(), new Date(),new Date(), dlt645ReadTask.getValues(),null);
                ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,null);
            }else{
                //下一条read指令
                dlt645ReadTask.setIdxReadInfo(dlt645ReadTask.getIdxReadInfo() + 1);
                Dlt645Var dlt645Var = dlt645ReadTask.getDlt645VarList().get(dlt645ReadTask.getIdxReadInfo());
                Dlt645Message dlt645Message = new Dlt645Message(
                        dlt645ReadTask.getProtocolEnum(),
                        dlt645ReadTask.getAddr(),
                        dlt645Var.getCode());
                channel.writeAndFlush(new DTUDownMeterMessage(dlt645Message.getEncodeBytes()));
            }

        }
    }

}
