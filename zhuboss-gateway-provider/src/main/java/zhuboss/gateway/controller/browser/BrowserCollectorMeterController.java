package zhuboss.gateway.controller.browser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.DateUtil;
import zhuboss.gateway.facade.vo.Item;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.util.MeterUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/collector/meter")
@Slf4j
public class BrowserCollectorMeterController {
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    MeterService meterService;
    @Autowired
    AppService appService;

    @RequestMapping("query")
    public GridTable<MeterPO> query(@RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                    @RequestParam(value="rows",defaultValue="10") Integer rows,
                                    String interfaceType,
                                    Integer collectorId,
                                    Integer meterKindId,
                                    Integer meterTypeId,
                                    Integer comPort,
                                    Integer addr,
                                    Integer enabled,
                                    Integer onlineFlag
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder();
        qcb.page(page, rows,null,null);
        qcb.andEqual("meter."+ MeterPO.Fields.APP_ID, UserSession.getAppId());
        if(StringUtils.hasText(interfaceType)){
            qcb.andEqual(MeterPO.Fields.INTERFACE_TYPE,interfaceType);
        }
        if(collectorId !=null){
            qcb.andEqual(MeterPO.Fields.COLLECTOR_ID,collectorId);
        }
        if(meterKindId != null){
            qcb.andSQL("meter_type_id in (select id from meter_type where meter_kind_id="+meterKindId+")");
        }
        if(meterTypeId != null){
            qcb.andEqual(MeterPO.Fields.METER_TYPE_ID, meterTypeId);
        }
        if(comPort != null){
            qcb.andEqual(MeterPO.Fields.COM_PORT,comPort);
        }
        if(addr != null){
            qcb.andEqual(MeterPO.Fields.ADDR,addr);
        }
        if(enabled != null){
            qcb.andEqual(MeterPO.Fields.ENABLED,enabled);
        }
        if(onlineFlag != null){
            qcb.andEqual(MeterPO.Fields.ONLINE_FLAG,onlineFlag);
            qcb.andSQL("( last_active_time is not null and last_active_time> '"+ DateUtil.toDateStr(appService.getGwLostTime(UserSession.getAppId()),DateUtil.sdf_yyyyMMddhhmmss )+"')");
        }
        List<MeterPO> list = meterPOMapper.selectByClause(qcb);
        for(MeterPO meterPO : list){
            meterService.setCycleSeconds(meterPO);
        }
        Integer cnt = meterPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    /**
     * 根据仪表类别、站点查询下面的仪表
     * @param meterKindId
     * @param stationId
     * @return
     */
    @GetMapping("droplist")
    public List<Item> dropList(Integer meterKindId,Integer stationId){
        if(meterKindId == null || stationId == null){
            return  new ArrayList<>();
        }
        List<MeterPO> meterPOList = meterPOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterPO.Fields.APP_ID,UserSession.getAppId())
                .andEqual(MeterPO.Fields.METER_KIND_ID,meterKindId)
                .andEqual(MeterPO.Fields.STATION_ID,stationId));
        List<Item> itemList = new ArrayList<>();
        for(MeterPO meterPO : meterPOList){
            itemList.add(new Item(meterPO.getId()+"", MeterUtil.getMeterName(meterPO)));
        }
        return itemList;
    }

}
