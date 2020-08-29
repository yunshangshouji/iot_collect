package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.AppPOMapper;
import zhuboss.gateway.mapper.CollectorPOMapper;
import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.param.SaveAppCfgParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cfg/app")
public class CfgAppController {

    @Autowired
    AppPOMapper appPOMapper;
    @Autowired
    CollectorPOMapper collectorPOMapper;
    @Autowired
    GatewayService gatewayService;
    @Autowired
    AppService appService;


    @RequestMapping("query")
    public GridTable<AppPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.andEqual(AppPO.Fields.APP_ID, UserSession.getAppId());

        List<AppPO> list = appPOMapper.selectByClause(qcb);
        Integer cnt = appPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid SaveAppCfgParam saveAppCfgParam) {
       appService.updateCfg(UserSession.getAppId(),saveAppCfgParam);
        //
        return new JsonResponse();
    }

}
