package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.controller.vo.MeterAlarmShowOrder;
import zhuboss.gateway.mapper.MeterAlarmPOMapper;
import zhuboss.gateway.po.MeterAlarmPO;
import zhuboss.gateway.po.MeterAlarmPOExt;
import zhuboss.gateway.service.MeterAlarmService;
import zhuboss.gateway.service.param.AddMeterAlarmParam;
import zhuboss.gateway.service.param.UpdateMeterAlarmParam;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value="/cfg/meter/alarm")
@Api(description = "设备告警条件")
@Slf4j
public class CfgMeterAlarmController {
    @Autowired
    MeterAlarmPOMapper meterAlarmPOMapper;
    @Autowired
    MeterAlarmService meterAlarmService;

    @RequestMapping(value="/query",method = RequestMethod.GET)
    @ApiOperation("列表")
    public GridTable<MeterAlarmPOExt> query(
            @RequestParam(value="page",required = false,defaultValue="1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer stationId,
            String meterKind
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page,rows,null, null);
        qcb.andEqual(MeterAlarmPO.Fields.APP_ID, UserSession.getAppId());
        qcb.sort("show_order IS  NULL,show_order").sort(MeterAlarmPO.Fields.METER_KIND_ID);
        if(stationId != null){
            qcb.andEqual(MeterAlarmPO.Fields.STATION_ID,stationId);
        }
        if(StringUtils.hasText(meterKind)){
            qcb.andEqual("meter_kind",meterKind);
        }
        GridTable<MeterAlarmPOExt> gridTable = meterAlarmService.query(qcb);
        return gridTable;
    }

    @RequestMapping(value="/add",method = RequestMethod.POST)
    @ApiOperation("新增告警条件")
    public JsonResponse add(@RequestBody @Valid AddMeterAlarmParam addDevAlarmParam) {
        meterAlarmService.addDevAlarm(UserSession.getAppId(),UserSession.getUserId(),addDevAlarmParam);
        return new JsonResponse();
    }

    @RequestMapping(value="/update",method = RequestMethod.POST)
    @ApiOperation("更新告警条件")
    public JsonResponse update(@RequestBody @Valid UpdateMeterAlarmParam updateDevAlarmParam) {
        meterAlarmService.updateDevAlarm(UserSession.getUserId(),updateDevAlarmParam);
        return new JsonResponse();
    }

    @RequestMapping(value="/delete",method = RequestMethod.GET)
    @ApiOperation("删除告警条件")
    public JsonResponse delete(Long id) {
        meterAlarmService.deleteDevAlarm(id);
        return new JsonResponse();
    }

    @RequestMapping(value="/save_order",method = RequestMethod.POST)
    @ApiOperation("保存排序")
    public JsonResponse saveOrder(@RequestBody List<MeterAlarmShowOrder> list){
        for(MeterAlarmShowOrder devAlarmShowOrder : list){
            if(devAlarmShowOrder.getId() == null){
                continue;
            }
            MeterAlarmPO meterAlarmPO = meterAlarmPOMapper.selectByPK(devAlarmShowOrder.getId());
            meterAlarmPO.setShowOrder(devAlarmShowOrder.getShowOrder());
            meterAlarmPOMapper.updateByPK(meterAlarmPO);
        }
        return new JsonResponse();
    }
}
