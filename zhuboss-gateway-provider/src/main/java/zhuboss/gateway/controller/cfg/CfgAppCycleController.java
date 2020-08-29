package zhuboss.gateway.controller.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.utils.BeanMapper;
import zhuboss.gateway.mapper.AppCyclePOMapper;
import zhuboss.gateway.po.AppCyclePO;
import zhuboss.gateway.service.AppCycleService;
import zhuboss.gateway.service.GatewayService;
import zhuboss.gateway.service.param.AddAppCycleParam;
import zhuboss.gateway.service.param.UpdateAppCycleParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/cfg/app/cycle")
public class CfgAppCycleController {
    @Autowired
    AppCycleService appCycleService;
    @Autowired
    AppCyclePOMapper appCyclePOMapper;
    @Autowired
    GatewayService gatewayService;


    @RequestMapping("query")
    public GridTable<AppCyclePO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows,
            Integer appId
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.andEqual(AppCyclePO.Fields.APP_ID, UserSession.getAppId());
        qcb.page(page, rows,null,null);
        if(appId != null){
            qcb.andEqual(AppCyclePO.Fields.APP_ID,appId);
        }
        List<AppCyclePO> list = appCyclePOMapper.selectByClause(qcb);
        appCycleService.sortReportLevel(list);
        Integer cnt = appCyclePOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddAppCycleParam addAppCycleParam) {
        AppCyclePO insert = new AppCyclePO();
        BeanMapper.copy(addAppCycleParam,insert);
        insert.setAppId(UserSession.getAppId());
        insert.setCreateTime(new Date());
        insert.setModifyTime(insert.getCreateTime());
        appCyclePOMapper.insert(insert);
        //刷新网关设备
        gatewayService.ifAppCycleChange(insert.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateAppCycleParam updateAppCycleParam) {
        AppCyclePO update = appCyclePOMapper.selectByPK(updateAppCycleParam.getId());
        BeanMapper.copy(updateAppCycleParam,update);
        update.setModifyTime(new Date());
        appCyclePOMapper.updateByPK(update);
        //刷新网关设备
        gatewayService.ifAppCycleChange(update.getAppId());
        return new JsonResponse();
    }

    @RequestMapping("delete")
    @WriteAction
    public JsonResponse delete(@RequestBody List<Integer> ids) {

        for(Integer id : ids) {
            AppCyclePO appCyclePO = appCyclePOMapper.selectByPK(id);
            Assert.isTrue(appCyclePO.getAppId().equals(UserSession.getAppId()));
            //刷新网关设备
            gatewayService.ifAppCycleChange(appCyclePO.getAppId());
            //物理记录
            appCyclePOMapper.deleteByPK(id);
        }


        return new JsonResponse();
    }

}
