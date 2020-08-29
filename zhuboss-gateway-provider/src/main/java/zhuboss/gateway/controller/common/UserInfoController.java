package zhuboss.gateway.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.service.UserService;
import zhuboss.gateway.service.param.ModifyUserInfo;
import zhuboss.gateway.service.param.ModifyUserPwd;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.util.mail.IEmailSendService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="user/info")
@Slf4j
public class UserInfoController {

    @Autowired
    UserPOMapper userPOMapper;
    @Autowired
    UserService userService;
    @Autowired
    IEmailSendService emailSendService;

    @GetMapping("getUserInfo")
    public Map<String,Object> getUserInfo(){
        UserPO userPO = userPOMapper.selectByPK(UserSession.getUserId());
        Map<String,Object> result = new HashMap<>();
        result.put("nickName",userPO.getNickName());
        result.put("mobile",userPO.getMobile());
        return result;
    }

    @RequestMapping("modifyUserInfo")
    @WriteAction
    public @ResponseBody JsonResponse modifyUserInfo(@Valid @RequestBody ModifyUserInfo modifyUserInfo){
        userService.modifyUserInfo(modifyUserInfo,UserSession.getUserId());
        return new JsonResponse();
    }

    @RequestMapping("modifyLoginPwd")
    @WriteAction
    public @ResponseBody JsonResponse modifyLoginPwd(@Valid @RequestBody ModifyUserPwd modifyUserPwd){
        userService.modifyUserPwd(modifyUserPwd,UserSession.getUserId());
        return new JsonResponse();
    }

}
