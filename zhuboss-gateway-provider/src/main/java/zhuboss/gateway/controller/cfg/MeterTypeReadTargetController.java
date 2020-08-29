package zhuboss.gateway.controller.cfg;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import zhuboss.gateway.mapper.MeterKindReadPOMapper;
import zhuboss.gateway.mapper.MeterTypeReadPOMapper;
import zhuboss.gateway.mapper.MeterTypeReadTargetPOMapper;
import zhuboss.gateway.po.MeterKindReadPO;
import zhuboss.gateway.po.MeterTypeReadPO;
import zhuboss.gateway.po.MeterTypeReadTargetPO;
import zhuboss.gateway.service.MeterTypeReadTargetService;
import zhuboss.gateway.service.vo.AddMeterTypeReadTargetParam;
import zhuboss.gateway.service.vo.UpdateMeterTypeReadTargetParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.dict.Item;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cfg/meter_type/read/target")
public class MeterTypeReadTargetController {
    @Autowired
    MeterTypeReadTargetService meterTypeReadTargetService;
    @Autowired
    MeterTypeReadPOMapper meterTypeReadPOMapper;
    @Autowired
    MeterTypeReadTargetPOMapper meterTypeReadTargetPOMapper;

    @Autowired
    MeterKindReadPOMapper meterKindReadPOMapper;

    @RequestMapping("query")
    public GridTable<MeterTypeReadTargetPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            @RequestParam(value = "readId",required = true) Integer readId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.andEqual(MeterTypeReadTargetPO.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page, rows,null,null)
                .sort("addr");
        if(readId !=null){
            qcb.andEqual(MeterTypeReadTargetPO.Fields.READ_ID,readId);
        }
        List<MeterTypeReadTargetPO> list = meterTypeReadTargetPOMapper.selectByClause(qcb);
        Integer cnt = meterTypeReadTargetPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody AddMeterTypeReadTargetParam param) {
        MeterTypeReadPO meterTypeReadPO = meterTypeReadPOMapper.selectByPK(param.getReadId());
        Assert.isTrue(meterTypeReadPO.getAppId().equals(UserSession.getAppId()));
        meterTypeReadTargetService.addMeterTypeTarget(param);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody UpdateMeterTypeReadTargetParam param) {
        meterTypeReadTargetService.updateMeterTypeTarget(param);
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            MeterTypeReadTargetPO meterTypeReadTargetPO = meterTypeReadTargetPOMapper.selectByPK(id);
            Assert.isTrue(meterTypeReadTargetPO.getAppId().equals(UserSession.getAppId()));
            meterTypeReadTargetService.deleteMeterTypeTargetId(id);
        }
        return new JsonResponse();
    }

    @RequestMapping("varDict")
    public List<Item> loadVarDict(@RequestParam(required = true) Integer meterKindId,String dlt){
        QueryClauseBuilder queryClauseBuilder = new QueryClauseBuilder()
                .andEqual(MeterKindReadPO.Fields.METER_KIND_ID,meterKindId)
                .sort(MeterKindReadPO.Fields.TARGET_CODE);
        if(StringUtils.hasText(dlt)){
            queryClauseBuilder.isNotNull(MeterKindReadPO.Fields.DLT645).andNotEqual(MeterKindReadPO.Fields.DLT645,"");
        }
        List<MeterKindReadPO> poList = meterKindReadPOMapper.selectByClause(queryClauseBuilder);
        List<Item> list = new ArrayList<>();
        for(MeterKindReadPO dictMeterTargetPO : poList){
            list.add(new Item(dictMeterTargetPO.getId()+"",dictMeterTargetPO.getTargetCode()+"-"+dictMeterTargetPO.getTargetName()));
        }
        return list;
    }
}
