package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.adapter.MeterTypeFactory;
import zhuboss.gateway.controller.console.param.MeterTypeCopyParam;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.mapper.MeterTypePOMapper;
import zhuboss.gateway.mapper.MeterTypeReadTargetPOMapper;
import zhuboss.gateway.po.MeterTypePO;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.service.param.AddMeterTypeParam;
import zhuboss.gateway.service.param.UpdateMeterTypeParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_type")
@Slf4j
public class MeterTypeController {
    @Autowired
    MeterTypePOMapper meterTypePOMapper;
    @Autowired
    MeterTypeReadTargetPOMapper meterTypeReadTargetPOMapper;
    @Autowired
    MeterTypeFactory meterTypeFactory;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    MeterTypeService meterTypeService;
    @Autowired
    GatewayService gatewayService;

    @RequestMapping("query")
    public GridTable<MeterTypePO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            @RequestParam(value="start",defaultValue="0")  Integer start,
            @RequestParam(value="limit",defaultValue="10") Integer limit,
            Integer plcFlag,
            Integer meterKindId,
            String typeName,
            Integer aliveFlag
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page,rows,start, limit).sort("id", ESortOrder.DESC);
        qcb.andEqual(MeterTypePO.Fields.APP_ID, UserSession.getAppId());
        if(plcFlag != null){
            qcb.andSQL("EXISTS(SELECT 1 FROM `meter_kind` WHERE id = meter_type.`meter_kind_id` AND plc_flag = "+plcFlag+")");
        }
        if(meterKindId != null){
            qcb.andEqual(MeterTypePO.Fields.METER_KIND_ID,meterKindId);
        }
        if(StringUtils.hasText(typeName)){
            qcb.andEqual(MeterTypePO.Fields.TYPE_NAME,typeName);
        }
        if(aliveFlag!=null){
            qcb.andEqual(MeterTypePO.Fields.ALIVE_FLAG,aliveFlag);
        }
        List<MeterTypePO> list = meterTypePOMapper.selectByClause(qcb);
        Integer cnt = meterTypePOMapper.selectCountByClause(qcb);
        return new GridTable<MeterTypePO>(list,cnt);
    }

    @GetMapping("filter")
    public List<Item> filter(@RequestParam(required = true) Integer meterKindId,
                             @RequestParam(required = true) String protocol){
        List<MeterTypePO> meterTypePOList = meterTypePOMapper.selectByClause(
                new QueryClauseBuilder()
                        .andEqual(MeterTypePO.Fields.METER_KIND_ID,meterKindId)
                        .andEqual(MeterTypePO.Fields.PROTOCOL,protocol)
                        .andEqual(MeterTypePO.Fields.APP_ID,UserSession.getAppId())
                        .sort("id",ESortOrder.DESC));
        List<Item> itemList = new ArrayList<>();
        for(MeterTypePO meterTypePO : meterTypePOList){
            Item item = new Item(meterTypePO.getId(),meterTypePO.getId()+"."+meterTypePO.getTypeName());
            itemList.add(item);
        }
        return  itemList;

    }

    @RequestMapping(value = "add")
    @WriteAction
    public JsonResponse addMeterType(@RequestBody AddMeterTypeParam addMeterTypeParam) {
        meterTypeService.addMeterType(addMeterTypeParam);
        return new JsonResponse();
    }

    @RequestMapping(value = "update")
    @WriteAction
    public JsonResponse update(@RequestBody UpdateMeterTypeParam updateMeterTypeParam) {
        meterTypeService.updateMeterType(updateMeterTypeParam);
        return new JsonResponse();
    }



    @RequestMapping("delete")
    @WriteAction
    @Transactional
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterTypePO meterTypePO = meterTypePOMapper.selectByPK(id);
            Assert.isTrue(meterTypePO.getAppId().equals(UserSession.getAppId()));
            meterTypeService.deleteMeterType(id);
        }
        return new JsonResponse();
    }

    @RequestMapping(value = "copy")
    @WriteAction
    public JsonResponse copy(@RequestBody MeterTypeCopyParam meterTypeCopyParam){
        meterTypeService.copy(meterTypeCopyParam);
        return new JsonResponse(true,"生效成功");
    }


    /**
     * 仪表型号
     * @return
     */
    @RequestMapping(value = "chooseMeterType",method = RequestMethod.GET)
    @ApiOperation("选择仪表型号")
    public List<Item> chooseMeterType(@ApiParam(value = "设备类别",required = true) @RequestParam(required = true) Integer meterKindId){
        List<Item> results = new ArrayList<>();
        List<MeterTypePO> list = meterTypePOMapper.selectByClause(new QueryClauseBuilder()
                .andEqual(MeterTypePO.Fields.APP_ID,UserSession.getAppId())
                .andEqual(MeterTypePO.Fields.METER_KIND_ID,meterKindId)
        );
        for(MeterTypePO meterTypePO : list){
            results.add(new Item(meterTypePO.getId(),meterTypePO.getId()+"."+meterTypePO.getTypeName()));
        }
        return results;
    }
}
