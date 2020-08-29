package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.HisViewSortPOMapper;
import zhuboss.gateway.po.HisViewColumnPO;
import zhuboss.gateway.po.HisViewSortPO;
import zhuboss.gateway.service.HisViewSortService;
import zhuboss.gateway.service.param.AddHisViewSortParam;
import zhuboss.gateway.service.param.UpdateHisViewSortParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/his_view/sort")
public class HisViewSortController {
    @Autowired
    HisViewSortService hisViewSortService;
    @Autowired
    HisViewSortPOMapper hisViewSortPOMapper;

    @RequestMapping("query")
    public GridTable<HisViewSortPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer hisViewId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual(HisViewSortPO.Fields.APP_ID, UserSession.getAppId()).sort(HisViewSortPO.Fields.SEQ);
        qcb.page(page, rows,null,null).sort(HisViewSortPO.Fields.SEQ) ;
        if(hisViewId != null){
            qcb.andEqual(HisViewSortPO.Fields.VIEW_ID,hisViewId);
        }
        List<HisViewSortPO> list = hisViewSortPOMapper.selectByClause(qcb);
        Integer cnt = hisViewSortPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @GetMapping("changeOrder")
    @WriteAction
    public JsonResponse changeOrder(Integer hisViewSortId, Integer num){
        hisViewSortService.changeOrder(hisViewSortId,num);
        return new JsonResponse();
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddHisViewSortParam addHisViewSortParam) {
        hisViewSortService.add(addHisViewSortParam, UserSession.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateHisViewSortParam updateHisViewSortParam) {
        hisViewSortService.update(updateHisViewSortParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            hisViewSortService.delete(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }

}
