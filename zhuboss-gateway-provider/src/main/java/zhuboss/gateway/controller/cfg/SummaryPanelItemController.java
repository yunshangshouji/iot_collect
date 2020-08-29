package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.SummaryPanelItemPOMapper;
import zhuboss.gateway.po.SummaryPanelItemPO;
import zhuboss.gateway.service.SummaryPanelItemService;
import zhuboss.gateway.service.param.AddSummaryPanelItemParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/summary/panel/item")
public class SummaryPanelItemController {
    @Autowired
    SummaryPanelItemPOMapper summaryPanelItemPOMapper;
    @Autowired
    SummaryPanelItemService summaryPanelItemService;

    @RequestMapping("query")
    public GridTable<SummaryPanelItemPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer summaryPanelId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual(SummaryPanelItemPO.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page, rows,null,null).sort(SummaryPanelItemPO.Fields.SEQ) ;
        if(summaryPanelId != null){
            qcb.andEqual(SummaryPanelItemPO.Fields.SUMMARY_PANEL_ID,summaryPanelId);
        }
        List<SummaryPanelItemPO> list = summaryPanelItemPOMapper.selectByClause(qcb);
        Integer cnt = summaryPanelItemPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @GetMapping("changeOrder")
    @WriteAction
    public JsonResponse changeOrder(Integer summaryPanelItemId,Integer num){
        summaryPanelItemService.changeOrder(summaryPanelItemId,num);
        return new JsonResponse();
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddSummaryPanelItemParam addSummaryPanelParam) {
        summaryPanelItemService.add(addSummaryPanelParam, UserSession.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            summaryPanelItemService.delete(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }

}
