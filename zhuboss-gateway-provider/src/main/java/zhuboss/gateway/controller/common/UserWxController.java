package zhuboss.gateway.controller.common;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.exception.BussinessException;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.spring.mvc.WriteAction;
import zhuboss.gateway.spring.web.filter.SessionKey;
import zhuboss.gateway.spring.web.filter.UserSession;
import zhuboss.gateway.wx.vo.BindParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="user/register")
@Slf4j
public class UserWxController {
    @Autowired
    LoginController loginController;

    @Autowired
    UserPOMapper userPOMapper;

    @GetMapping("wx/bind/info")
    @ApiOperation("绑定信息")
    public String info(ModelMap map){
        String openId = UserSession.getSessionAttr(SessionKey.OPEN_ID);
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.OPENID,openId));
        map.put("user",userPO);
        return "wx/bind_info";
    }

    @RequestMapping(value="wx/bind/bind")
    @ApiOperation("申请绑定")
    @WriteAction
    public @ResponseBody JsonResponse bind(@RequestBody BindParam bindParam){
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,bindParam.getMail()));
        if(userPO == null){
            throw new BussinessException("邮箱不存在"+bindParam.getMail());
        }
        if(!userPO.getLoginPwd().equals(DigestUtils.md5Hex(bindParam.getLoginPwd()))){
            throw new BussinessException("密码验证失败");
        }
        String openId = UserSession.getSessionAttr(SessionKey.OPEN_ID);
        userPO.setOpenid(openId);
        userPOMapper.updateByPK(userPO);
        return new JsonResponse(true,"绑定成功");
    }

    @RequestMapping(value="wx/bind/unbind")
    @ApiOperation("解除绑定")
    @WriteAction
    public @ResponseBody JsonResponse unbind(HttpServletRequest request){
        String openId = UserSession.getSessionAttr(SessionKey.OPEN_ID);
        UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.OPENID,openId));
        if(userPO == null){
            throw new BussinessException("当前用户尚未绑定账户， 无解绑操作");
        }
        userPO.setOpenid(null);
        userPOMapper.updateByPK(userPO);
        /**
         * 退出登录状态
         */
        loginController.logout(request);
        return new JsonResponse(true,"解除绑定成功");
    }


}
