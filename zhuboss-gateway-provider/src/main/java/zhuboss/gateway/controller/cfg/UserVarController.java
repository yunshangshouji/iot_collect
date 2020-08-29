package zhuboss.gateway.controller.cfg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.ESortOrder;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.UserVarPOMapper;
import zhuboss.gateway.po.UserVarPO;
import zhuboss.gateway.service.UserVarService;
import zhuboss.gateway.service.param.AddUserVarParam;
import zhuboss.gateway.service.param.UpdateUserVarParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.LoginFilter;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value="/cfg/user/var")
@Api(description = "站点变量")
@Slf4j
public class UserVarController {
    @Autowired
    UserVarPOMapper stationVarPOMapper;
    @Autowired
    UserVarService stationVarService;

    @RequestMapping(value="/query",method = RequestMethod.GET)
    @ApiOperation("综保列表")
    public GridTable<UserVarPO> query(
            @RequestParam(value="page",required = false,defaultValue="1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page,rows,null, null);
        qcb.andEqual(UserVarPO.Fields.APP_ID, UserSession.getAppId());
        qcb.sort("id", ESortOrder.DESC);

        List<UserVarPO> list = stationVarPOMapper.selectByClause(qcb);
        Integer cnt = stationVarPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping(value="/add",method = RequestMethod.POST)
    @ApiOperation("新增")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddUserVarParam addStationVarParam) {
        stationVarService.add(UserSession.getAppId(),addStationVarParam);
        return new JsonResponse();
    }

    @RequestMapping(value="/update",method = RequestMethod.POST)
    @ApiOperation("修改")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateUserVarParam updateUserVarParam) {
        stationVarService.update(updateUserVarParam);
        return new JsonResponse();
    }

    @RequestMapping(value="/delete",method = RequestMethod.GET)
    @ApiOperation("物理删除")
    @WriteAction
    public JsonResponse delete(Integer id) {
        stationVarService.delete(UserSession.getAppId(),id);
        return new JsonResponse();
    }

}
