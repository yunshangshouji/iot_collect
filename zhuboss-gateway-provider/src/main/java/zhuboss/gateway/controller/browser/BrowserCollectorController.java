package zhuboss.gateway.controller.browser;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.MatchMode;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.DateUtil;
import zhuboss.framework.utils.tree.CommonTree;
import zhuboss.gateway.dict.MeterTreeTypeEnum;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.facade.constants.InterfaceTypeEnum;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.TxCollectorPOMapper;
import zhuboss.gateway.po.*;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.param.AddCollectorParam;
import zhuboss.gateway.service.param.UpdateCollectorParam;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.gateway.raw.scheduler.TaskDownScheduler;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackage;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo.ZhubossMeta;
import zhuboss.gateway.util.MeterUtil;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/collector")
@Slf4j
public class BrowserCollectorController {
    @Autowired
    GatewayService gatewayService;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    TxCollectorPOMapper txCollectorPOMapper;
    @Autowired
    CollectorService collectorService;
    @Autowired
    TaskDownScheduler taskDownScheduler;
    @Autowired
    AppService appService;

    @RequestMapping("query")
    public GridTable<CollectorPO> query(@RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                        @RequestParam(value="rows",defaultValue="10") Integer rows,
                                        String devNo,
                                        Integer stationId,
                                        Integer onlineFlag
                                        ){
        QueryClauseBuilder qcb =new QueryClauseBuilder();
        qcb.page(page, rows,null,null);
        qcb.andEqual(CollectorPO.Fields.APP_ID, UserSession.getAppId());
        qcb.sort(CollectorPO.Fields.ID, ESortOrder.DESC);
        if(StringUtils.hasText(devNo)){
            qcb.andLike(CollectorPO.Fields.DEV_NO,devNo, MatchMode.ANYWHERE);
        }
        if(stationId != null){
            qcb.andEqual(CollectorPO.Fields.STATION_ID,stationId);
        }
        Date gwLostTime = appService.getGwLostTime(UserSession.getAppId());
        if(onlineFlag != null){
            if(onlineFlag == 0){
                qcb.andSQL("( last_active_time is null or last_active_time< '"+ DateUtil.toDateStr(gwLostTime,DateUtil.sdf_yyyyMMddhhmmss )+"')");
            }else{
                qcb.andSQL("( last_active_time > '"+ DateUtil.toDateStr(gwLostTime,DateUtil.sdf_yyyyMMddhhmmss )+"')");
            }
        }
        List<CollectorPO> list = collectorPOMapper.selectByClause(qcb);
        for(CollectorPO collectorPO : list){
            collectorPO.setOnlineFlag((collectorPO.getLastActiveTime()==null || gwLostTime.compareTo(collectorPO.getLastActiveTime())>0)?0:1);
        }
        Integer cnt = collectorPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @GetMapping("collect_now")
    public JsonResponse collectNow(String devNo) throws IOException {
        Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
        if(channel == null){
            return new JsonResponse(false,"网关不在线");
        }
        CollectorTypeEnum collectorType = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_TYPE);
        if(CollectorTypeEnum.isRAW(collectorType)){
            taskDownScheduler.run(channel);
        }else if(collectorType.equals(CollectorTypeEnum.ZHUBOSS)){
            ZhubossDataPackage zhubossDataPackage = new ZhubossDataPackage(ZhubossPackageType.COLLECT_REPORT,new byte[]{});
            channel.writeAndFlush(zhubossDataPackage);
        }
        return new JsonResponse(true,"成功下达命令");
    }

    /*@GetMapping("tree")
    public Object collectorTree(Integer collectorId){
        CollectorPO collectorPO = collectorPOMapper.selectByPK(collectorId);
        List<MeterPO> meterPOList = meterPOMapper.selectByClause(new QueryClauseBuilder().andEqual(MeterPO.Fields.COLLECTOR_ID,collectorId)
                .sort(MeterPO.Fields.INTERFACE_TYPE)
                .sort(MeterPO.Fields.COM_PORT)
                .sort(MeterPO.Fields.IP)
                .sort(MeterPO.Fields.PORT)
        );
        Map<String,Object> result = new HashMap<>();
        result.put("name",collectorPO.getGwNo()+collectorPO.getDevName());
        result.put("meterSize",meterPOList.size()); //用于给前端计算容器的高度
        List<Map<String,Object>> childrens = new ArrayList<>();
        result.put("children",childrens);
        MeterPO preMeter = null;
        Map<String,Object> inter = null;
        List<Map<String,Object>> meterList = null;
        for(MeterPO meterPO : meterPOList){
            if(preMeter == null || !compareInter(meterPO,preMeter)){
                inter = new HashMap<>();
                childrens.add(inter);
                meterList = new ArrayList<>();
                inter.put("name",getInterfaceName(meterPO));
                inter.put("children",meterList);
            }
            Map<String,Object> meter = new HashMap<>();
            meter.put("name", MeterUtil.getMeterName(meterPO));
            meterList.add(meter);
            preMeter = meterPO;
        }
        return result;
    }*/

    private String getInterfaceName(MeterPO meterPO){
        InterfaceTypeEnum typeEnum = InterfaceTypeEnum.getByCode(meterPO.getInterfaceType());
        String name = typeEnum.getText();
        if(typeEnum.equals(InterfaceTypeEnum.COM)){
            return name + meterPO.getComPort();
        }else if(typeEnum.equals(InterfaceTypeEnum.PLC)){
            return name;
        }else if(typeEnum.equals(InterfaceTypeEnum.TCP)){
            return name + meterPO.getHost()+":"+meterPO.getPort();
        }
        return null;
    }

    private boolean compareInter(MeterPO meterPO1,MeterPO meterPO2){
        if(!meterPO1.getInterfaceType().equals(meterPO2.getInterfaceType())){
            return false;
        }
        if(meterPO1.getInterfaceType().equals(InterfaceTypeEnum.COM.getCode()) && !meterPO1.getComPort().equals(meterPO2.getComPort())){
            return false;
        }
        if(meterPO1.getInterfaceType().equals(InterfaceTypeEnum.PLC.getCode())){
            return true;
        }
        if(meterPO1.getInterfaceType().equals(InterfaceTypeEnum.TCP.getCode()) &&
                (!meterPO1.getHost().equals(meterPO2.getHost()) || !meterPO1.getPort().equals(meterPO2.getPort()))){
            return false;
        }
        return true;
    }

}
