package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.SummaryPOMapper;
import zhuboss.gateway.po.SummaryPO;
import zhuboss.gateway.service.SummaryService;
import zhuboss.gateway.service.param.AddSummaryParam;
import zhuboss.gateway.service.param.UpdateSummaryParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/summary")
public class SummaryController {
    @Autowired
    SummaryPOMapper summaryPOMapper;
    @Autowired
    SummaryService summaryService;

    @RequestMapping("query")
    public GridTable<SummaryPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual(SummaryPO.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page, rows,null,null).sort(SummaryPO.Fields.SEQ) ;
        List<SummaryPO> list = summaryPOMapper.selectByClause(qcb);
        Integer cnt = summaryPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @GetMapping("changeOrder")
    @WriteAction
    public JsonResponse changeOrder(Integer summaryId,Integer num){
        summaryService.changeOrder(summaryId,num);
        return new JsonResponse();
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddSummaryParam addSummaryParam) {
        summaryService.add(addSummaryParam, UserSession.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateSummaryParam updateSummaryParam) {
        summaryService.update(updateSummaryParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            summaryService.delete(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }

}
