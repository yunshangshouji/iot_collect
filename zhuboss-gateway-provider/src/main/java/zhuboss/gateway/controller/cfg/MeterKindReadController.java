package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.MeterKindReadPOMapper;
import zhuboss.gateway.po.MeterKindWritePO;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.po.MeterPO;
import zhuboss.gateway.service.MeterKindReadService;
import zhuboss.gateway.service.param.AddMeterKindReadParam;
import zhuboss.gateway.service.param.UpdateMeterKindReadParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_kind/read")
public class MeterKindReadController {
    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;

    @Autowired
    MeterKindReadService meterKindReadService;


    @RequestMapping("query")
    public GridTable<MeterKindReadPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer persistFlag,
            @RequestParam(required = true) Integer meterKindId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page, rows,null,null);
        qcb.andEqual(MeterKindReadPO.Fields.APP_ID, UserSession.getAppId());
        qcb.sort(MeterKindReadPO.Fields.TARGET_CODE);
        qcb.andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId);
        if(persistFlag!=null){
            qcb.andEqual(MeterKindReadPO.Fields.PERSIST_FLAG,persistFlag);
        }
        List<MeterKindReadPO> list = meterKindReadPOMapper.selectByClause(qcb);
        Integer cnt = meterKindReadPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddMeterKindReadParam addMeterKindReadParam) {
        meterKindReadService.add(addMeterKindReadParam);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateMeterKindReadParam updateMeterKindTargetParam) {
        meterKindReadService.update(updateMeterKindTargetParam);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterKindReadPO meterKindReadPO = meterKindReadPOMapper.selectByPK(id);
            Assert.isTrue(meterKindReadPO.getAppId().equals(UserSession.getAppId()));
            meterKindReadService.delete(id);
        }
        return new JsonResponse();
    }

    @RequestMapping("/persist/enable")
    @WriteAction
    public JsonResponse enable(@RequestBody List<Integer> ids) {
        return able(true,ids);
    }

    @RequestMapping("/persist/disable")
    @WriteAction
    public JsonResponse disable(@RequestBody List<Integer> ids) {
        return  able(false,ids);
    }


    private JsonResponse able(boolean enable, List<Integer> ids) {
        Assert.isTrue(ids.size()>0);
        MeterKindReadPO meterPO = null;
        Integer meterKindId = null;
        for(Integer id : ids) {
            meterPO = meterKindReadPOMapper.selectByPK(id);
            Assert.isTrue(meterPO.getAppId().equals(UserSession.getAppId()));
            Assert.isTrue(meterKindId == null || meterKindId.equals(meterPO.getMeterKindId())); //一个批次中应该全是一个类别下的字段
            meterKindId = meterPO.getMeterKindId();
            meterPO.setPersistFlag(1);

        }
        meterKindReadService.ifPersistFlagsChange(meterKindId,enable,ids);
        return new JsonResponse();
    }

}
