package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.HisViewPOMapper;
import zhuboss.gateway.po.HisViewPO;
import zhuboss.gateway.service.HisViewService;
import zhuboss.gateway.service.param.AddHisViewParam;
import zhuboss.gateway.service.param.UpdateHisViewParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/his_view")
public class HisViewController {
    @Autowired
    HisViewService hisViewService;
    @Autowired
    HisViewPOMapper hisViewPOMapper;

    @RequestMapping("query")
    public GridTable<HisViewPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual(HisViewPO.Fields.APP_ID, UserSession.getAppId()).sort(HisViewPO.Fields.SEQ);
        qcb.page(page, rows,null,null).sort(HisViewPO.Fields.SEQ) ;
        List<HisViewPO> list = hisViewPOMapper.selectByClause(qcb);
        Integer cnt = hisViewPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @GetMapping("changeOrder")
    @WriteAction
    public JsonResponse changeOrder(Integer hisViewId, Integer num){
        hisViewService.changeOrder(hisViewId,num);
        return new JsonResponse();
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddHisViewParam addHisViewParam) {
        hisViewService.add(addHisViewParam, UserSession.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateHisViewParam updateHisViewParam) {
        hisViewService.update(updateHisViewParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            hisViewService.delete(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }


}
