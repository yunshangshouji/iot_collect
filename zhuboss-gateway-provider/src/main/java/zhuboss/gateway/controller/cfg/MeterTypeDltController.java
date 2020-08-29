package zhuboss.gateway.controller.cfg;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.MeterTypeDltPOMapper;
import zhuboss.gateway.po.MeterTypeDltPO;
import zhuboss.gateway.service.MeterTypeDltService;
import zhuboss.gateway.service.param.AddMeterTypeDltParam;
import zhuboss.gateway.spring.mvc.WriteAction;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_type/dlt645")
public class MeterTypeDltController {
    @Autowired
    MeterTypeDltPOMapper meterTypeDltPOMapper;
    @Autowired
    MeterTypeDltService meterTypeDltService;

    @RequestMapping("query")
    public GridTable<MeterTypeDltPO> query(
            @RequestParam(value="start",defaultValue="0")  Integer start,
            @RequestParam(value="limit",defaultValue="100") Integer limit,
            @RequestParam(value = "meterTypeId",required = true) Integer meterTypeId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(start, limit);
        if(meterTypeId !=null){
            qcb.andEqual(MeterTypeDltPO.Fields.METER_TYPE_ID,meterTypeId);
        }
        List<MeterTypeDltPO> list = meterTypeDltPOMapper.selectByClause(qcb);
        Integer cnt = meterTypeDltPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddMeterTypeDltParam param) {
        meterTypeDltService.add(param);
        return new JsonResponse();
    }


    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            meterTypeDltService.delete(id);
        }
        return new JsonResponse();
    }


}
