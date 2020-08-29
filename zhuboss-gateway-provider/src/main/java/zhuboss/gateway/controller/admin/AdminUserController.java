package zhuboss.gateway.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.GridTable;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.service.UserService;
import zhuboss.gateway.service.param.AddUserParam;
import zhuboss.gateway.service.param.SetUserPwdParam;
import zhuboss.gateway.service.param.UpdateUserParam;
import zhuboss.gateway.spring.mvc.WriteAction;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Autowired
    UserService userService;
    @Autowired
    UserPOMapper userPOMapper;
    
    @RequestMapping("query")
    public GridTable<UserPO> query(
            @RequestParam(value="page",required = false,defaultValue = "1")  Integer page,
            @RequestParam(value="rows",defaultValue="10") Integer rows
    ) {
        QueryClauseBuilder qcb = new QueryClauseBuilder();
        qcb.page(page, rows,null,null) ;
        List<UserPO> list = userPOMapper.selectByClause(qcb);
        Integer cnt = userPOMapper.selectCountByClause(qcb);
        return new GridTable<>(list,cnt);
    }

    @RequestMapping("add")
    @WriteAction
    public JsonResponse add(@RequestBody @Valid AddUserParam addUserParam) {
        userService.add(addUserParam);
        return new JsonResponse();
    }

    @RequestMapping("update")
    @WriteAction
    public JsonResponse update(@RequestBody @Valid UpdateUserParam updateUserParam) {
        userService.update(updateUserParam);
        //
        return new JsonResponse();
    }

    @RequestMapping("set_pwd")
    @WriteAction
    public JsonResponse setPwd(@RequestBody @Valid SetUserPwdParam setUserPwdParam) {
        userService.setPwd(setUserPwdParam);
        //
        return new JsonResponse();
    }
    
}
