package zhuboss.gateway.controller.cfg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.MatchMode;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.MeterPOMapper;
import zhuboss.gateway.mapper.MeterKindWritePOMapper;
import zhuboss.gateway.mapper.MeterKindReadPOMapper;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.service.AppCycleService;
import zhuboss.gateway.service.MeterService;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cfg/tx/read")
public class CfgTxReadController {
    @Autowired
    MeterPOMapper meterPOMapper;
    @Autowired
    MeterKindWritePOMapper meterKindWritePOMapper;
    @Autowired
    AppCycleService appCycleService;
    @Autowired
    MeterService meterService;

    @GetMapping("query")
    public GridTable<MeterPO> query(@RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
                                    @RequestParam(value="rows",defaultValue="20") Integer rows,
                                    Integer stationId,
                                    Integer meterKindId,
                                    String devNo,
                                    Integer comPort,
                                    Integer addr,
                                    Integer enabled,
                                    Integer onlineFlag,
                                    String data,
                                    ////排序
                                    String sort,
                                    String order
    ){
        QueryClauseBuilder qcb =new QueryClauseBuilder();
        qcb.page(page,rows,null, null);

        qcb.andEqual(MeterPO.Fields.APP_ID, UserSession.getAppId());
        if(stationId != null){
            qcb.andEqual(MeterPO.Fields.STATION_ID,stationId);
        }
        if(meterKindId != null){
            qcb.andEqual(MeterPO.Fields.METER_KIND_ID,meterKindId);
        }
        if(StringUtils.hasText(devNo)){
            qcb.andLike(MeterPO.Fields.DEV_NO,devNo,MatchMode.ANYWHERE);
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
        }
        if(StringUtils.hasText(data)){
            qcb.andLike("data",data, MatchMode.ANYWHERE);
        }
        /**
         * 排序
         */
        if(StringUtils.hasText(sort) && StringUtils.hasText(order) ){
            if(sort.equals("readTime")){
                qcb.sort("read_time", ESortOrder.valueOf(order.toUpperCase()));
            }
            if(sort.equals("addr")){
                qcb.sort("ter_id", ESortOrder.valueOf(order.toUpperCase()));
            }
        }else{
            qcb.sort(MeterPO.Fields.COM_PORT).sort(MeterPO.Fields.ADDR);
        }
        List<MeterPO> list = meterPOMapper.selectByClause(qcb);
        for(MeterPO meterPO : list){
            meterService.setCycleSeconds(meterPO);
        }
        Integer cnt = meterPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping(value = "test_read",method = RequestMethod.GET)
    public JsonResponse testWrite(@RequestParam(required = true) Integer id) throws InterruptedException {
        JsonResponse jsonResponse = meterService.read(System.currentTimeMillis()+"",id,true);
        return jsonResponse;

    }

}
