package zhuboss.gateway.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.shiro.SessionUtil;
import zhuboss.gateway.controller.console.vo.LoginParam;
import zhuboss.gateway.mapper.AppPOMapper;
import zhuboss.gateway.mapper.UserAppPOMapper;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.AppPO;
import zhuboss.gateway.po.UserAppPO;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.service.AppService;
import zhuboss.gateway.service.vo.CheckUserApp;
import zhuboss.gateway.spring.shiro.ShiroConfig;
import zhuboss.gateway.spring.web.filter.SessionKey;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="")
@Slf4j
public class LoginController {

	@Value("${admin.password}")
	private String ADMIN_PASSWORD;
	@Autowired
	AppService appService;
	@Autowired
	AppPOMapper appPOMapper;
	@Autowired
	UserPOMapper userPOMapper;
	@Autowired
	UserAppPOMapper userAppPOMapper;

	@RequestMapping(value="/login/ajaxLogin")
	public @ResponseBody JsonResponse doLoginAjax(@RequestBody LoginParam loginParam,HttpServletRequest request){
		loginParam.setUsername(loginParam.getUsername().trim());
		String page;
		Integer userId;
		boolean isSingleAppCust = false;
		if (loginParam.getUsername().equals("admin")){
			if(!loginParam.getPassword().equals(ADMIN_PASSWORD)){
				return new JsonResponse(false,"密码错误");
			}
			page = "/static/admin/admin.html";
			userId = 0; //代表平台用户
		}else{
			UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.MAIL,loginParam.getUsername()));
			if(userPO == null){
				return new JsonResponse(false,"用户不存在");
			}
			if(!DigestUtils.md5Hex(loginParam.getPassword()).equals(userPO.getLoginPwd())){
				return new JsonResponse(false,"密码错误");
			}
			userId = userPO.getId();
			/**
			 * 非运营商，
			 * 1. 只有一个项目且为项目用户，直接进入项目站点
			 * 2. 有多个项目，显示项目列表，每个项目可浏览进入
			 * 2.1 如果是项目的配置员，则有“配置链接”
			 * 2.2 如果是项目的owner，则有修改、删除权限
			 */
			List<UserAppPO> userAppPOList = userAppPOMapper.selectByClause(new QueryClauseBuilder().andEqual(UserAppPO.Fields.USER_ID,userPO.getId()));
			isSingleAppCust = userAppPOList.size() == 1 && userAppPOList.get(0).getCfgFlag()==0 ;
			if(isSingleAppCust){
				page = "/static/browser/browser.html?appId="+userAppPOList.get(0).getAppId();
			}else{
				//显示项目列表页面
				page = "/static/app_list.html";
			}
		}

		if(ShiroConfig.enable()){
			//登录验证
			UsernamePasswordToken token = new UsernamePasswordToken(loginParam.getUsername(), loginParam.getPassword());
			try {
				SecurityUtils.getSubject().login(token);
			}
			catch(AuthenticationException e) {
				log.error("登录失败",e);
				return new JsonResponse(false,"登录失败");
			}
		}
		UserSession.setSessionAttr(SessionKey.USER_ID,userId);
		return  new JsonResponse(true,"登录成功",page);
	}

	@RequestMapping(value = "/logout",method = {RequestMethod.GET,RequestMethod.POST})
	public String logout(HttpServletRequest request){
		boolean shiro = ShiroConfig.enable();
		if(shiro){
			SessionUtil.getSubject().logout();
		}
		request.getSession().removeAttribute(SessionKey.APP_ID.name());
		 return "redirect:/static/login.html";
	}

}
