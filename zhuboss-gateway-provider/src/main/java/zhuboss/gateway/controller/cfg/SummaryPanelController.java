package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.SummaryPanelPOMapper;
import zhuboss.gateway.po.SummaryPanelPO;
import zhuboss.gateway.service.SummaryPanelService;
import zhuboss.gateway.service.param.AddSummaryPanelParam;
import zhuboss.gateway.service.param.AddSummaryParam;
import zhuboss.gateway.service.param.UpdateSummaryPanelParam;
import zhuboss.gateway.service.param.UpdateSummaryParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/summary/panel")
public class SummaryPanelController {
    @Autowired
    SummaryPanelPOMapper summaryPanelPOMapper;
    @Autowired
    SummaryPanelService summaryPanelService;

    @RequestMapping("query")
    public GridTable<SummaryPanelPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer summaryId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder().andEqual(SummaryPanelPO.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page, rows,null,null).sort(SummaryPanelPO.Fields.SEQ) ;
        if(summaryId != null){
            qcb.andEqual(SummaryPanelPO.Fields.SUMMARY_ID,summaryId);
        }
        List<SummaryPanelPO> list = summaryPanelPOMapper.selectByClause(qcb);
        Integer cnt = summaryPanelPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @GetMapping("changeOrder")
    @WriteAction
    public JsonResponse changeOrder(Integer summaryPanelId,Integer num){
        summaryPanelService.changeOrder(summaryPanelId,num);
        return new JsonResponse();
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddSummaryPanelParam addSummaryPanelParam) {
        summaryPanelService.add(addSummaryPanelParam, UserSession.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateSummaryPanelParam updateSummaryPanelParam) {
        summaryPanelService.update(updateSummaryPanelParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            summaryPanelService.delete(UserSession.getAppId(),id);
        }
        return new JsonResponse();
    }

}
