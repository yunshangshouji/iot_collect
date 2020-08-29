package zhuboss.gateway.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.shiro.SessionUtil;
import zhuboss.framework.utils.IpUtil;
import zhuboss.gateway.controller.console.param.SendForgetCode;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.service.UserService;
import zhuboss.gateway.service.param.UserRegisterParam;
import zhuboss.gateway.service.param.UserValidateParam;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.shiro.ShiroConfig;
import zhuboss.gateway.spring.web.filter.SessionKey;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.util.mail.IEmailSendService;
import zhuboss.gateway.util.mail.SendEmailMessage;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value="user/register")
@Slf4j
public class UserRegisterController {
    public static final String FORGET_MAIL = "forgetMail";
    public static final String FORGET_CODE = "forgetCode";

    @Autowired
    UserPOMapper userPOMapper;
    @Autowired
    IEmailSendService emailSendService;
    @Autowired
    UserService userService;

    @RequestMapping(value="")
    public @ResponseBody JsonResponse register(@RequestBody  @Valid UserRegisterParam param, HttpServletRequest request){
        String ip = IpUtil.getIpAddr(request);
        try {
            userService.register(param,ip);
            return  new JsonResponse();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return new JsonResponse(false,e.getMessage());
        }

    }

    @RequestMapping(value="validate")
    @WriteAction
    public @ResponseBody JsonResponse validate(@RequestBody  @Valid UserValidateParam param){
        Integer userId = userService.validte(param);
        //完成登录
        if(ShiroConfig.enable()){
            //登录验证
            UsernamePasswordToken token = new UsernamePasswordToken(param.getMail(), param.getVerifyCode());
            try {
                SecurityUtils.getSubject().login(token);
            }
            catch(AuthenticationException e) {
                log.error("登录失败",e);
                return new JsonResponse(false,"登录失败");
            }
        }
        UserSession.setSessionAttr(SessionKey.USER_ID,userId);
        return  new JsonResponse();
    }

    @RequestMapping("sendForgetCode")
    @WriteAction
    public @ResponseBody JsonResponse sendForgetCode(@Valid @RequestBody SendForgetCode sendForgetCode) throws UnsupportedEncodingException, MessagingException {
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,sendForgetCode.getMail()).andEqual(UserPO.Fields.VALID_FLAG,1));
        if(userPO == null){
            throw new BussinessException("邮箱不存在");
        }

        //发送邮件
        String code = userService.getRand(10);
        SendEmailMessage sendEmailMessage = new SendEmailMessage();
        sendEmailMessage.setAddress(sendForgetCode.getMail());
        sendEmailMessage.setSubject("密码重置");
        sendEmailMessage.setContent("您的密码重置验证码："+code);
        emailSendService.sendEmail(sendEmailMessage);
        //验证码，记录session
        SessionUtil.setSessionAttr(FORGET_MAIL,sendForgetCode.getMail());
        SessionUtil.setSessionAttr(FORGET_CODE,code);

        return new JsonResponse();
    }

    @RequestMapping("resetPwd")
    @WriteAction
    public @ResponseBody JsonResponse resetPwd(@Valid @RequestBody SendForgetCode sendForgetCode) throws UnsupportedEncodingException, MessagingException {
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,sendForgetCode.getMail()).andEqual(UserPO.Fields.VALID_FLAG,1));
        if(userPO == null){
            throw new BussinessException("邮箱不存在");
        }

        String mail = (String)SessionUtil.getSessionAttr(FORGET_MAIL);
        String code = (String)SessionUtil.getSessionAttr(FORGET_CODE);
        if(!mail.equals(sendForgetCode.getMail()) || !code.equals(sendForgetCode.getCode())){
            throw new BussinessException("邮箱或验证码错误");
        }
        userService.resetPwd(userPO.getId(),sendForgetCode.getLoginPwd());
        SessionUtil.removeAttribute(FORGET_MAIL);
        SessionUtil.removeAttribute(FORGET_CODE);


        return new JsonResponse();
    }


}
