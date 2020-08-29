package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.MeterKindPOMapper;
import zhuboss.gateway.mapper.MeterKindWritePOMapper;
import zhuboss.gateway.po.MeterKindPO;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.service.MeterKindWriteService;
import zhuboss.gateway.service.param.AddMeterKindWriteParam;
import zhuboss.gateway.service.param.UpdateMeterKindWriteParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_kind/write")
public class MeterKindWriteController {
    @Autowired
    MeterKindPOMapper meterKindPOMapper;
    @Autowired
    MeterKindWritePOMapper meterKindWritePOMapper;
    @Autowired
    MeterKindWriteService meterKindWriteService;

    @RequestMapping("query")
    public GridTable<MeterKindWritePO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            @RequestParam(required = true) Integer meterKindId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page, rows,null,null);
        qcb.andEqual(MeterKindWritePO.Fields.APP_ID, UserSession.getAppId());
        qcb.sort(MeterKindWritePO.Fields.TARGET_CODE);
        qcb.andEqual(MeterKindWritePO.Fields.METER_KIND_ID,meterKindId);
        List<MeterKindWritePO> list = meterKindWritePOMapper.selectByClause(qcb);
        Integer cnt = meterKindWritePOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddMeterKindWriteParam addMeterKindSignalParam) {
        MeterKindWritePO insert = new MeterKindWritePO();
        BeanMapper.copy(addMeterKindSignalParam,insert);
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        MeterKindPO meterKindPO = meterKindPOMapper.selectByPK(addMeterKindSignalParam.getMeterKindId());
        Assert.isTrue(meterKindPO.getAppId().equals(UserSession.getAppId()));
        insert.setAppId(meterKindPO.getAppId());
        meterKindWritePOMapper.insert(insert);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateMeterKindWriteParam updateMeterKindSignalParam) {
        MeterKindWritePO update = meterKindWritePOMapper.selectByPK(updateMeterKindSignalParam.getId());
        BeanMapper.copy(updateMeterKindSignalParam,update);
        update.setModifyTime(new Date());
        meterKindWritePOMapper.updateByPK(update);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterKindWritePO meterKindWritePO = meterKindWritePOMapper.selectByPK(id);
            Assert.isTrue(meterKindWritePO.getAppId().equals(UserSession.getAppId()));
            meterKindWriteService.delete(id);
        }
        return new JsonResponse();
    }

}
