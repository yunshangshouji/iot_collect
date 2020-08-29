package zhuboss.gateway.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zhuboss.framework.utils.ObjectId;
import zhuboss.gateway.adapter.TaskScheduler;
import zhuboss.gateway.facade.mq.message.CollectorOfflineMessage;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.TxCollectorPOMapper;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.po.TxCollectorPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.tx.channel.MyChannelGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 开发环境不能运行的原因，没有设备接入将会把所有数据刷新成离线状态，与线上服务冲突
 */
@ConditionalOnProperty(name = "collector.online.check", matchIfMissing = false)
@Component
@Slf4j
public class CollectorOnlineCheckScheduler {
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    TxCollectorPOMapper txCollectorPOMapper;
    @Autowired
    CollectorService collectorService;
    @Autowired
    TaskScheduler taskScheduler;

    @Scheduled(cron = "*/10 * * * * ?")
    public void run() {
        /**
         * 1. 每10秒钟检测一次网关在线状态，与数据库状态比对，如果不一致，则同步一次；
         * 2. 发现网关离线，数据库在线，推送离线事件
         * 3. 如果网关离线，则每60秒发送一次事件，避免事件未成功传递；
         *
         */
        boolean minuteTimeOut = new Date().equals(DateUtils.truncate(new Date(),Calendar.MINUTE));
        List<CollectorPO> collectorPOList = collectorService.getAllCollectors();
        for(CollectorPO collectorPO : collectorPOList){
            boolean online = MyChannelGroup.allChannels.findChannelByDevNo(collectorPO.getDevNo())!=null;
            boolean sendMsg = false;
            boolean dbOnline = collectorPO.getOnlineFlag()!=null && collectorPO.getOnlineFlag() ==1; //数据库在线状态
            if(online && !dbOnline){
                //改成上线状态
                TxCollectorPO update = txCollectorPOMapper.selectByPK(collectorPO.getId());
                update.setOnlineFlag(1);
                update.setLastOnlineTime(new Date());
                txCollectorPOMapper.updateByPK(update);

            }else if (online == false && dbOnline){
                //改成离线状态
                TxCollectorPO update = txCollectorPOMapper.selectByPK(collectorPO.getId());
                update.setOnlineFlag(0);
                update.setOfflineTime(new Date());
                txCollectorPOMapper.updateByPK(update);
                sendMsg = true;

            }

            //每隔一分钟，离线的网关强制发送事件，避免应用系统消息丢失
            if((minuteTimeOut && !online) || sendMsg){
                CollectorOfflineMessage collectorOfflineMessage = new CollectorOfflineMessage();
                collectorOfflineMessage.setId(ObjectId.get());
                collectorOfflineMessage.setHappenTime(new Date());
                collectorOfflineMessage.setCollectorId(collectorPO.getId());
                taskScheduler.receiveCollectorOfflineMessage(collectorPO.getAppId(), collectorOfflineMessage);
            }
        }

    }

}
