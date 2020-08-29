package zhuboss.gateway.controller.cfg;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.MatchMode;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.framework.utils.DateUtil;
import zhuboss.gateway.facade.constants.CollectorTypeEnum;
import zhuboss.gateway.mapper.TxCollectorPOMapper;
import zhuboss.gateway.po.TxCollectorPO;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.param.UpdateLoraCfgParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.tx.channel.ChannelKeys;
import zhuboss.gateway.tx.gateway.raw.scheduler.TaskDownScheduler;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.tx.channel.MyChannelGroup;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossDataPackage;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.ZhubossPackageType;
import zhuboss.gateway.tx.gateway.smart.provider.zhuboss.downvo.ZhubossMeta;
import zhuboss.gateway.po.CollectorPO;
import zhuboss.gateway.service.CollectorService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.param.AddCollectorParam;
import zhuboss.gateway.service.param.UpdateCollectorParam;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/cfg/collector")
@Slf4j
public class CfgCollectorController {
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

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddCollectorParam addCollectorParam) {
        collectorService.addCollector(UserSession.getAppId(),addCollectorParam);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateCollectorParam updateCollectorParam) {
        collectorService.updateCollector(updateCollectorParam);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            collectorService.deleteById(id);
        }
        return new JsonResponse();
    }

    /**
     * 调取采集统计
     * @return
     */
    @GetMapping("retrieve_sts")
    public JsonResponse retrieveSts(String devNo) throws InterruptedException {
        Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
        if(channel == null){
            return new JsonResponse(false,"网关不在线");
        }
        CollectorTypeEnum collectorType = ChannelKeys.readAttr(channel,ChannelKeys.COLLECTOR_TYPE);
        if(!collectorType.equals(CollectorTypeEnum.ZHUBOSS)){
            return new JsonResponse(false,"只有智能网关才可以调取");
        }
        ZhubossDataPackage zhubossDataPackage = new ZhubossDataPackage(ZhubossPackageType.RETRIEVE_STS,new byte[]{});
        channel.writeAndFlush(zhubossDataPackage);
        synchronized (channel){
            channel.wait(15*1000); // 最大等待时间20秒
        }
        String json = ChannelKeys.readAttr(channel,ChannelKeys.RETRIEVE_STS);
        if(json!=null){
            ChannelKeys.setAttr(channel,ChannelKeys.RETRIEVE_STS,null);
            return new JsonResponse(true,json);
        }else{
            return new JsonResponse(false,"应答超时");
        }

    }

    @GetMapping("view_down_meta")
    public JsonResponse viewDownMeta(String devNo){
        ZhubossMeta zhubossMeta = gatewayService.getDownMeta(devNo);
        return new JsonResponse(true, JSON.toJSONString(zhubossMeta));
    }

    @GetMapping("do_down_meta")
    @WriteAction
    public JsonResponse doDownMeta(String devNo){
        Channel channel = MyChannelGroup.allChannels.findChannelByDevNo(devNo);
        if(channel == null){
            return  new JsonResponse(false,"网关不在线");
        }
        gatewayService.doDownMeta(devNo);
        return new JsonResponse(true,"下发成功");
    }

    @GetMapping("dict")
    public List<Item> collectorList(String collectorType){
        QueryClauseBuilder queryClauseBuilder = new QueryClauseBuilder().sort(CollectorPO.Fields.DEV_NO);
        if(StringUtils.hasText(collectorType)){
            queryClauseBuilder.andEqual(CollectorPO.Fields.COLLECTOR_TYPE,collectorType);
        }
        List<CollectorPO> collectorPOList = collectorPOMapper.selectByClause(queryClauseBuilder);
        List<Item> results = new ArrayList<>();
        for(CollectorPO collectorPO : collectorPOList){
            results.add(new Item(collectorPO.getId()+"",collectorPO.getDevNo()));
        }
        return results;
    }

    @GetMapping("clearError")
    public JsonResponse clearError(Integer collectorId){
        Map<String,Object> map = new HashMap<>();
        map.put(TxCollectorPO.Fields.COLLECTOR_ID.name(),collectorId);
        map.put(TxCollectorPO.Fields.DEV_ERROR_MSG.name(),null);
        map.put(TxCollectorPO.Fields.DEV_ERROR_TIME.name(),null);
        txCollectorPOMapper.insertOrUpdate(map);
        return new JsonResponse();
    }

    @RequestMapping("getLoraCfg")
    public UpdateLoraCfgParam getLoraCfg(Integer collectorId){
        CollectorPO collectorPO = collectorPOMapper.selectByPK(collectorId);
        UpdateLoraCfgParam data = new UpdateLoraCfgParam();
        BeanMapper.copy(collectorPO,data);
        return data;
    }

    @RequestMapping("updateLoraCfg")
    @WriteAction
    public JsonResponse updateLoraCfg(@RequestBody @Valid UpdateLoraCfgParam updateLoraCfgParam){
        collectorService.updateLoraCfg(updateLoraCfgParam);
        return new JsonResponse<>();
    }

}
