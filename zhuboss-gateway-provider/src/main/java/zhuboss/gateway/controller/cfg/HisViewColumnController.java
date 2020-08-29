package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.facade.vo.DataId;
import zhuboss.gateway.mapper.HisViewColumnPOMapper;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.po.HisViewColumnPO;
import zhuboss.gateway.po.HisViewPO;
import zhuboss.gateway.service.HisViewColumnService;
import zhuboss.gateway.service.MeterTypeService;
import zhuboss.gateway.service.param.AddHisViewColumnParam;
import zhuboss.gateway.service.param.UpdateHisViewColumnParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/his_view/column")
public class HisViewColumnController {
    @Autowired
    HisViewColumnService hisViewColumnService;
    @Autowired
    HisViewPOMapper hisViewPOMapper;
    @Autowired
    HisViewColumnPOMapper hisViewColumnPOMapper;
    @Autowired
    MeterTypeService meterTypeService;

    @RequestMapping("query")
    public GridTable<HisViewColumnPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer hisViewId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual(HisViewColumnPO.Fields.APP_ID, UserSession.getAppId()).sort(HisViewColumnPO.Fields.SEQ);
        qcb.page(page, rows,null,null).sort(HisViewColumnPO.Fields.SEQ) ;
        if(hisViewId != null){
            qcb.andEqual(HisViewColumnPO.Fields.VIEW_ID,hisViewId);
        }
        List<HisViewColumnPO> list = hisViewColumnPOMapper.selectByClause(qcb);
        Integer cnt = hisViewColumnPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @GetMapping("changeOrder")
    @WriteAction
    public JsonResponse changeOrder(Integer hisViewColumnId, Integer num){
        hisViewColumnService.changeOrder(hisViewColumnId,num);
        return new JsonResponse();
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddHisViewColumnParam addHisViewColumnParam) {
        hisViewColumnService.add(addHisViewColumnParam, UserSession.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateHisViewColumnParam updateHisViewColumnParam) {
        hisViewColumnService.update(updateHisViewColumnParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            hisViewColumnService.delete(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }

    @RequestMapping(value="/vars",method = RequestMethod.GET)
    @ApiOperation("变量字典")
    public List<DataId> vars(@RequestParam(required = true) Integer hisViewId){
        HisViewPO hisViewPO =  hisViewPOMapper.selectByPK(hisViewId);
        List<DataId> itemList = meterTypeService.queryMeterKindVar(hisViewPO.getMeterKindId(),true);
        for(DataId dataId : itemList){
            dataId.setText(dataId.getValue()+"【"+dataId.getText()+"】");
        }
        return itemList;
    }

}
