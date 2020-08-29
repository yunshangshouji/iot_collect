package zhuboss.gateway.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.MatchMode;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.AppPOMapper;
import zhuboss.gateway.mapper.UserAppPOMapper;
import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.po.UserAppPO;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.param.SaveAppParam;
import zhuboss.gateway.service.vo.CheckUserApp;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.SessionKey;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    AppPOMapper appPOMapper;
    @Autowired
    AppService appService;
    @Autowired
    UserAppPOMapper userAppPOMapper;


    @RequestMapping("query")
    public GridTable<UserAppPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer owner,
            String appName
    ) {
        Integer cnt;
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page, rows,null,null).sort("app.create_time", ESortOrder.DESC) ;
        Integer userId = UserSession.getUserId();
        qcb.andEqual(UserAppPO.Fields.USER_ID,userId);
        //全部项目
        if(owner != null && owner == 1){
            qcb.andEqual(UserAppPO.Fields.OWNER_FLAG,1);
        }else if(owner == 2){
            qcb.andEqual(UserAppPO.Fields.OWNER_FLAG,2);
        }
        if(StringUtils.hasText(appName)){
            qcb.andLike(UserAppPO.Fields.APP_NAME,appName, MatchMode.ANYWHERE);
        }
        List<UserAppPO> userAppPOList = userAppPOMapper.selectByClause(qcb);
        cnt = appPOMapper.selectCountByClause(qcb);

        return new GridTable<>(userAppPOList,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid SaveAppParam saveAppParam) {
        appService.add(saveAppParam, UserSession.getUserId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid SaveAppParam saveAppParam) {
        appService.update(saveAppParam, UserSession.getUserId());
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {
        for(Integer id : ids) {
            appService.delete(id);
        }
        return new JsonResponse();
    }

    @GetMapping("login/app")
    public @ResponseBody JsonResponse<Map<String,Object>> loginApp(@RequestParam(required = true) Integer appId, Boolean cfg){
        Map<String,Object>  map = new HashMap<>();
        if(cfg == null){
            cfg = false;
        }
        CheckUserApp checkUserApp = appService.checkUserApp(UserSession.getUserId(),appId);
        if(checkUserApp == null){
            return new JsonResponse<>(false,"无权访问");
        }
        if(cfg && checkUserApp.isCfg() ==false){
            return new JsonResponse<>(false,"无权访问");
        }
        if(checkUserApp.isCfg()){
            UserSession.setSessionAttr(SessionKey.CFG_ABLE,true);
        }else{
            UserSession.setSessionAttr(SessionKey.CFG_ABLE,false);
        }
        UserSession.setSessionAttr(SessionKey.APP_ID,appId);
        AppPO appPO = appPOMapper.selectByPK(appId);
        map.put("appName",appPO.getAppName());
        map.put("cfgAble",checkUserApp.isCfg());
        return  new JsonResponse<>(true,"成功",map);
    }
}
