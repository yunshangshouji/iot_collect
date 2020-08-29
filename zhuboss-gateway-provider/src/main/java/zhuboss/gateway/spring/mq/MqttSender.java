package zhuboss.gateway.spring.mq;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhuboss.gateway.facade.mq.ClassMesssage;
import zhuboss.gateway.facade.mq.message.BaseMessage;

import java.util.ArrayList;
import java.util.List;

@Component
public class MqttSender {
    @Autowired
    private MqttGateway mqttGateway;

    /**
     * 临时过滤机制
     */
    public static final List<Pause> pauseList = new ArrayList<>();

    public void sendToMqtt(Integer appId,Integer meterId, BaseMessage message){
        /**
         * 广播
         */
        //TODO 根据MQTT监控页面的websocket设置过滤拦截
        /*for(Pause pause : pauseList){
            if(
                    (!StringUtils.hasText(pause.getCollectorId()) || pause.getCollectorId().equals(txPO.getDevNo()))
                    && (pause.getInterNo()==null || pause.getInterNo().equals(txPO.getComPort()))
                     && (!StringUtils.hasText(pause.getTerId()) || pause.getTerId().equals(txPO.getAddr()))
            ){
                return;
            }
        }*/
        ClassMesssage classMesssage = new ClassMesssage();
        classMesssage.setClassName(message.getClass().getName());
        classMesssage.setMessage(message);
        String json = JSON.toJSONString(classMesssage);
        mqttGateway.sendToMqtt("/"+appId+"/"+message.getCollectorId(),json );

        /**
         * 记录日志
         */
       /* TxMqPO txMqPO = new TxMqPO();
        txMqPO.setMsgId(txPO.getId());
        txMqPO.setAppId(txPO.getAppId());
        txMqPO.setCollectorId(txPO.getGwNo());
        txMqPO.setIp(txPO.getIp());
        txMqPO.setPort(txPO.getPort());
        txMqPO.setComPort(txPO.getComPort());
        txMqPO.setAddr(txPO.getAddr());
        txMqPO.setPath(path);
        txMqPO.setType(type.name());
        txMqPO.setTs(new Date());
        txMqPO.setContent(json);
        txMqPOMapper.replace(txMqPO);*/
    }
}
