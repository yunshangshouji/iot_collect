package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.controller.console.param.PersistConfigParam;
import zhuboss.gateway.mapper.HisDataMapper;
import zhuboss.gateway.mapper.MeterKindPOMapper;
import zhuboss.gateway.po.MeterKindPO;
import zhuboss.gateway.service.DDLService;
import zhuboss.gateway.service.MeterKindService;
import zhuboss.gateway.service.param.AddMeterKindParam;
import zhuboss.gateway.service.param.UpdateMeterKindParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_kind")
public class MeterKindController {

    @Autowired
    MeterKindPOMapper meterKindPOMapper;
    @Autowired
    MeterKindService meterKindService;
    @Autowired
    DDLService ddlService;
    @Autowired
    HisDataMapper hisDataMapper;

    @RequestMapping("query")
    public GridTable<MeterKindPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page, rows,null,null);
        qcb.andEqual(MeterKindPO.Fields.APP_ID, UserSession.getAppId());
        qcb.sort(MeterKindPO.Fields.KIND_CODE); //设置了SEQ值排前,1~ N 升序
        List<MeterKindPO> list = meterKindPOMapper.selectByClause(qcb);
        for(MeterKindPO meterKindPO : list){
            if(meterKindPO.getPersistFlag() ==0 ){
                continue;
            }
            BigDecimal tableBytes = hisDataMapper.queryTableBytes(meterKindPO.getId());
            meterKindPO.setTableBytes(tableBytes);
            BigDecimal yesterdayPercent = hisDataMapper.queryYesterdayPercent(meterKindPO.getId());
            meterKindPO.setYesterdayTableBytes(tableBytes.multiply(yesterdayPercent).setScale(2, RoundingMode.HALF_UP));

        }
        Integer cnt = meterKindPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddMeterKindParam addMeterKindParam) {
        meterKindService.add(UserSession.getAppId(),addMeterKindParam);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateMeterKindParam updateMeterKindParam) {
        meterKindService.update(updateMeterKindParam);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterKindPO meterKindPO = meterKindPOMapper.selectByPK(id);
            Assert.isTrue(meterKindPO.getAppId().equals(UserSession.getAppId()));
            meterKindService.delete(id);
        }
        return new JsonResponse();
    }

    @RequestMapping("persist_config")
    @ApiOperation("存储配置")
    @WriteAction
    public JsonResponse savePersistConfig(@RequestBody @Valid PersistConfigParam persistConfigParam){
        meterKindService.updatePersistConfig(persistConfigParam);
        return new JsonResponse();
    }

    @RequestMapping("clear_history")
    @ApiOperation("清空历史数据")
    @WriteAction
    public JsonResponse savePersistConfig(Integer meterKindId){
        ddlService.truncateTable(meterKindId);
        return new JsonResponse();
    }

}
