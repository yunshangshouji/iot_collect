package zhuboss.gateway.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.UserAppPOMapper;
import zhuboss.gateway.po.UserAppPO;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.param.AddAppUserParam;
import zhuboss.gateway.service.param.UpdateAppUserParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/app/user")
public class AppUserController {
    @Autowired
    UserAppPOMapper userAppPOMapper;
    @Autowired
    AppService appService;

    @RequestMapping("query")
    public GridTable<UserAppPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            @RequestParam(required = true) Integer appId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page, rows,null,null).sort(UserAppPO.Fields.CREATE_TIME, ESortOrder.DESC) ;
        Integer userId = UserSession.getUserId();
        qcb.andEqual(UserAppPO.Fields.APP_ID,appId).andEqual(UserAppPO.Fields.OWNER_FLAG,0);
        List<UserAppPO> userAppPOList = userAppPOMapper.selectByClause(qcb);
        Integer cnt = userAppPOMapper.selectCountByClause(qcb);
        return new GridTable<>(userAppPOList,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse addAppUser(@RequestBody @Valid AddAppUserParam addAppUserParam) {
        appService.addAppUser(addAppUserParam, UserSession.getUserId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse updateAppUser(@RequestBody @Valid UpdateAppUserParam updateAppUserParam) {
        appService.updateAppUser(updateAppUserParam, UserSession.getUserId());
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse deleteAppUser(@RequestBody List<Integer> ids) {
        for(Integer id : ids){
            appService.deleteAppUser(id,UserSession.getUserId());
        }
        return new JsonResponse();
    }
}
