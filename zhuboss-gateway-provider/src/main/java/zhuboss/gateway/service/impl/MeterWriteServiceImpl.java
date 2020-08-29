package zhuboss.gateway.service.impl;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.utils.JavaUtil;
import zhuboss.gateway.adapter.ModbusUtil;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.tx.channel.task.MyStack;
import zhuboss.gateway.tx.channel.task.modbus.ModbusWriteTask;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.MeterTypeWritePOMapper;
import zhuboss.gateway.mapper.LogWritePOMapper;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.task.DeviceRequestMessage;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.MeterTypeWritePO;
import zhuboss.gateway.po.LogWritePO;
import zhuboss.gateway.service.MeterWriteService;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackage;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.meter.modbus.ModbusMessage;
import zhuboss.gateway.util.MeterUtil;

import java.util.Date;

@Service
public class MeterWriteServiceImpl implements MeterWriteService {
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterTypeWritePOMapper meterTypeWritePOMapper;
    @Autowired
    LogWritePOMapper logWritePOMapper;

    @Override
    public JsonResponse write(String taskUUID,Integer meterTypeWriteId, Integer collectorMeterId,String writeData,boolean waitResp) throws InterruptedException {
        MeterPO meterPO = meterPOMapper.selectByPK(collectorMeterId);
        CollectorPO collectorPO = collectorPOMapper.selectByPK(meterPO.getCollectorId());
        String devNo = meterPO.getDevNo();
        Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
        if(channel == null){
            return new JsonResponse(false,"设备不在线");
        }

        /**
         * 存入任务队列
         */
        MeterTypeWritePO meterTypeWritePO = meterTypeWritePOMapper.selectByPK(meterTypeWriteId);
        String dataHex = StringUtils.hasText(writeData)?writeData : meterTypeWritePO.getDataHex();
        if(!StringUtils.hasText(dataHex)){
            throw new BussinessException("写入值不能为空");
        }
        ModbusUtil.checkWrite(meterTypeWritePO.getCmd(),meterTypeWritePO.getAddr(),meterTypeWritePO.getWriteUnits(),meterTypeWritePO.getWriteByteSize(),dataHex);
        byte[] bytes = ModbusUtil.buildData(meterTypeWritePO.getCmd(),meterTypeWritePO.getAddr(),meterTypeWritePO.getWriteUnits(),meterTypeWritePO.getWriteByteSize(),dataHex);
        ModbusWriteTask modbusWriteTask = new ModbusWriteTask(taskUUID,meterTypeWritePO.getCmd(),bytes);
        modbusWriteTask.setAddr(meterPO.getAddr().intValue());

        if(CollectorTypeEnum.isRAW(collectorPO.getCollectorType())){
            // 透传放入任务队列
            //TODO 要有优先级的插入
            MyStack<DeviceRequestMessage> taskStack = ChannelKeys.readAttr(channel, ChannelKeys.REQUEST_STACK);
            if (taskStack == null) {
                return new JsonResponse(false,"taskStack为空");
            }
            taskStack.push(modbusWriteTask);
        }else{
            // 智能网关，直接执行，由gateway provider encode、decode
            ChannelKeys.setAttr(channel,ChannelKeys.EXECUTING_TASK,modbusWriteTask);
            ModbusMessage modbusMessage = new ModbusMessage((byte)modbusWriteTask.getAddr(),(byte)modbusWriteTask.getCmd(),modbusWriteTask.getWriteBytes());
            String base64Data = Base64Utils.encodeToString(modbusMessage.getEncodeBytes());
            ZhubossDataPackage zhubossDataPackage = new ZhubossDataPackage(ZhubossPackageType.WRITE, meterPO.getComPort()+","+ meterPO.getMeterTypeId()+","+base64Data);
            channel.writeAndFlush(zhubossDataPackage);
        }


        /**
         * 记录到数据库
         */
        LogWritePO logWritePO = new LogWritePO();
        logWritePO.setAppId(meterPO.getAppId());
        logWritePO.setTaskUuid(taskUUID);
        logWritePO.setMeterKindId(meterPO.getMeterKindId());
        logWritePO.setMeterId(meterPO.getId());
        logWritePO.setMeterName(MeterUtil.getMeterName(meterPO));
        logWritePO.setTargetCode(meterTypeWritePO.getTargetCode());
        logWritePO.setTargetName(meterTypeWritePO.getTargetName());
        logWritePO.setGwNo(meterPO.getDevNo());
        logWritePO.setAddr(meterPO.getAddr());
        logWritePO.setCmd(meterTypeWritePO.getCmd());
        logWritePO.setDataHex(JavaUtil.bytesToHexString(bytes));
        logWritePO.setCreateTime(new Date());
        logWritePOMapper.insert(logWritePO);

        if(!waitResp){
            return new JsonResponse(true,"执行成功!");
        }

        /**
         * 定时器轮询获得响应结果
         */
        synchronized (modbusWriteTask){
            modbusWriteTask.wait(10*1000); // 最大等待时间20秒
        }
        if(modbusWriteTask.getReceive() == null){
            return new JsonResponse(false,"应答超时，请检查抄表读数核对执行结果");
        }

        String res = String.format("Addr:%02x,Function:%02x,Raw:%s",modbusWriteTask.getReceive().getAdr(),modbusWriteTask.getReceive().getFunCode(),JavaUtil.bytesToHexString(modbusWriteTask.getReceive().getData()));
        if(modbusWriteTask.getReceive().getFunCode() == modbusWriteTask.getCmd()){
            return new JsonResponse(true,res);
        }else{
            return new JsonResponse(false, res);
        }
    }

}
