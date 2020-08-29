package zhuboss.gateway.controller.cfg;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.MeterTypePlcReadPOMapper;
import zhuboss.gateway.po.MeterTypeDltPO;
import zhuboss.gateway.po.MeterTypePlcReadPO;
import zhuboss.gateway.service.MeterTypePlcReadService;
import zhuboss.gateway.service.param.AddMeterTypeDltParam;
import zhuboss.gateway.service.param.AddMeterTypePlcReadParam;
import zhuboss.gateway.service.param.UpdateMeterTypePlcReadParam;
import zhuboss.gateway.service.vo.UpdateMeterTypeWriteParam;
import zhuboss.gateway.spring.mvc.WriteAction;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_type/plc/read")
public class MeterTypePlcController {
    @Autowired
    MeterTypePlcReadPOMapper meterTypePlcReadPOMapper;
    @Autowired
    MeterTypePlcReadService meterTypePlcReadService;

    @RequestMapping("query")
    public GridTable<MeterTypePlcReadPO> query(
            @RequestParam(value="start",defaultValue="0")  Integer start,
            @RequestParam(value="limit",defaultValue="100") Integer limit,
            @RequestParam(value = "meterTypeId",required = true) Integer meterTypeId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(start, limit);
        if(meterTypeId !=null){
            qcb.andEqual(MeterTypeDltPO.Fields.METER_TYPE_ID,meterTypeId);
        }
        List<MeterTypePlcReadPO> list = meterTypePlcReadPOMapper.selectByClause(qcb);
        Integer cnt = meterTypePlcReadPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddMeterTypePlcReadParam addMeterTypePlcReadParam) {
        meterTypePlcReadService.add(addMeterTypePlcReadParam);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody UpdateMeterTypePlcReadParam updateMeterTypePlcReadParam) {
        meterTypePlcReadService.update(updateMeterTypePlcReadParam);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            meterTypePlcReadService.delete(id);
        }
        return new JsonResponse();
    }


}
