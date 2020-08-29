package zhuboss.gateway.controller.browser;

import io.netty.channel.Channel;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.DateUtil;
import zhuboss.gateway.common.HourSts;
import zhuboss.gateway.common.HourStsHour;
import zhuboss.gateway.common.HourStsResult;
import zhuboss.gateway.controller.console.vo.CollectorFlow;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;

import java.util.*;

@RestController
@RequestMapping("dashboard")
public class DashboardController {
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    AppService appService;

    @GetMapping("dev_count")
    public Map<String,Integer> gatewaySts(){
        Map<String,Integer> map = new HashMap<>();
        /**
         * 网关
         */
        QueryClauseBuilder qcb1 = new QueryClauseBuilder();
        qcb1.andEqual(CollectorPO.Fields.APP_ID, UserSession.getAppId());
        Integer totalCount = collectorPOMapper.selectCountByClause(qcb1);
        map.put("gwTotalCount",totalCount);
        qcb1.andSQL("( last_active_time is null or last_active_time< '"+ DateUtil.toDateStr(appService.getGwLostTime(UserSession.getAppId()),DateUtil.sdf_yyyyMMddhhmmss )+"')");
        Integer gwOfflineCount = collectorPOMapper.selectCountByClause(qcb1);
        map.put("gwOfflineCount",gwOfflineCount);
        map.put("gwOnlineCount",totalCount - gwOfflineCount);

        /**
         * 总数量
         */
        Integer meterTotalCount = meterPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.APP_ID,UserSession.getAppId()));
        map.put("meterTotalCount",meterTotalCount);

        /**
         * 停用的仪表
         */
        Integer meterDisableCount = meterPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.APP_ID,UserSession.getAppId()).andEqual(MeterPO.Fields.ENABLED,0));
        map.put("meterDisableCount",meterDisableCount);

        /**
         * 仪表
         */
        //仪表离线
        Integer meterOfflineCount1 = meterPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.APP_ID,UserSession.getAppId())
                .andEqual(MeterPO.Fields.ENABLED,1)
                .andEqual(MeterPO.Fields.ONLINE_FLAG,0)
                .andSQL("( last_active_time is not null and last_active_time>= '"+ DateUtil.toDateStr(appService.getGwLostTime(UserSession.getAppId()),DateUtil.sdf_yyyyMMddhhmmss )+"')")
        );
        map.put("meterOfflineCount1",meterOfflineCount1);
        //网关离线
        Integer meterOfflineCount2 = meterPOMapper.selectCountByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.APP_ID,UserSession.getAppId())
                .andEqual(MeterPO.Fields.ENABLED,1)
                .andSQL("( last_active_time is null or last_active_time< '"+ DateUtil.toDateStr(appService.getGwLostTime(UserSession.getAppId()),DateUtil.sdf_yyyyMMddhhmmss )+"')")
        );
        map.put("meterOfflineCount2",meterOfflineCount2);
        //在线仪表
        map.put("meterOnlineCount",meterTotalCount - meterDisableCount - meterOfflineCount1 - meterOfflineCount2);
        return map;
    }

    @GetMapping("sts")
    public Map<String, Object> eventSts(){
        Integer appId = UserSession.getAppId();
        Map<String, Object> results = new HashMap<>();
        // 越限&恢复
        results.put("overLimit", HourSts.getHourSts(appId).queryOverLimit());
        results.put("overLimitResume", HourSts.getHourSts(appId).queryOverLimitResume());
        // 遥信变位
        results.put("signal", HourSts.getHourSts(appId).querySignal());
        // 24小时TCP总流量
        List<HourStsResult> tcpFlowUpper = HourSts.getHourSts(appId).queryTcpFlowUpper();
        List<HourStsResult> tcpFlowDown = HourSts.getHourSts(appId).queryTcpFlowDown();
        long max = 0;
        for(HourStsResult item : tcpFlowUpper){
            if(item.getCount()>max){
                max = item.getCount();
            }
        }
        for(HourStsResult item : tcpFlowDown){
            if(item.getCount()>max){
                max = item.getCount();
            }
        }
        String unit;
        int ratio= 1;
        if(max > (1024l*1024l*1024l*10l)){
            unit = "GB";
            ratio = 1024*1024*1024;
        }else if(max> (1024*1024*10) ){
            unit = "MB";
            ratio = 1024*1024;
        }else if(max> (1024*10) ){
            unit = "KB";
            ratio = 1024;
        }else{
            unit = "B";
        }
        for(HourStsResult item : tcpFlowUpper){
            item.setCount(item.getCount()/ratio);
        }
        for(HourStsResult item : tcpFlowDown){
            item.setCount(item.getCount()/ratio);
        }
        results.put("tcpFlowUpper", tcpFlowUpper);
        results.put("tcpFlowDown", tcpFlowDown);
        results.put("tcpFlowUnit",unit);

        //TCP流量：UpperMax、UpperMin、DonwMax、DownMin

        return results;
    }

    private List<CollectorFlow> flowList(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.addHours(new Date(),-1)); //上一个小时
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        List<CollectorFlow> collectorFlowList = new ArrayList<>();
        Iterator<Channel> iterator = MyChannelGroup.allChannels.iterator();
        while(iterator.hasNext()){
            Channel channel = iterator.next();
            String devNo = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_NO);
            if(!StringUtils.hasText(devNo)){
                continue;
            }
            HourStsHour[] down = ChannelKeys.readAttr(channel,ChannelKeys.tcpDownFlowSts);
            HourStsHour[] upper = ChannelKeys.readAttr(channel,ChannelKeys.tcpUpperFlowSts);
            collectorFlowList.add(new CollectorFlow(devNo,down[hour].getCount(day),upper[hour].getCount(day)));

        }

        Collections.sort(collectorFlowList, new Comparator<CollectorFlow>() {
            @Override
            public int compare(CollectorFlow o1, CollectorFlow o2) {
                return (int)(o1.getUpperBytes() + o1.getDownBytes() - o2.getUpperBytes() - o2.getDownBytes());
            }
        });
        return collectorFlowList;
    }

    @RequestMapping("max_flow")
    public GridTable<CollectorFlow> maxFlow() {
        List<CollectorFlow> collectorFlowList = this.flowList();
        //移除中间部分
        List<CollectorFlow> results = new ArrayList<>();
        for(int i=0;i<5 && i<collectorFlowList.size();i++){
            results.add(collectorFlowList.get(i));
        }
        return new GridTable<>(results,results.size());
    }

    @RequestMapping("min_flow")
    public GridTable<CollectorFlow> minFlow() {
        List<CollectorFlow> collectorFlowList = this.flowList();
        //移除中间部分
        List<CollectorFlow> results = new ArrayList<>();
        for(int i=collectorFlowList.size()-1;i>-1;i--){
            results.add(collectorFlowList.get(i));
        }
        return new GridTable<>(results,results.size());
    }


}
