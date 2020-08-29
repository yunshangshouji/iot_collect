package zhuboss.gateway.spring.web.filter;

import zhuboss.framework.shiro.SessionUtil;
import zhuboss.gateway.controller.common.LoginController;
import zhuboss.gateway.spring.shiro.ShiroConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;


/**
 * 判断用户是否已经登录，如果已经登录则重定向到首页
 */
public class LoginFilter implements Filter{
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse) response;
		UserSession.request = req;
		/**
		 * 公开资源，不需登录
		 */
		String requestURI = req.getRequestURI();
		if(requestURI.equals("/") ||requestURI.equals("/api") || requestURI.startsWith("/common/") ||requestURI.startsWith("/static/") || requestURI.startsWith("/favicon.ico")
				|| requestURI.startsWith("/login") || requestURI.startsWith("/logout")|| requestURI.startsWith("/user/register")
				|| requestURI.startsWith("/weixin") //微信管理
				|| requestURI.startsWith("/_wx") //微信跳转获取open id
				|| requestURI.startsWith("/win_proxy")
		){
			filterChain.doFilter(request, response);
			return;
		}

		//未登录，访问login
		boolean shiro = ShiroConfig.enable(); //放在后面，导致/common/sys/dict.js不能用session
		if(
				(shiro && !SessionUtil.getSubject().isAuthenticated())
				|| (!shiro && ((HttpServletRequest) request).getSession().getAttribute(SessionKey.USER_ID.name()) == null)

		){
			res.setStatus(401);
			res.setHeader("SessionStatus","not_login");
			res.setHeader("SessionStatusText", URLEncoder.encode("未登录","UTF-8"));
			return;
		}

		/**
		 * 已登录，需要验证资源访问权限
		 */
		Boolean isAdmin = UserSession.isAdmin();
		if(
				(requestURI.startsWith("/admin/") && (isAdmin == null || !isAdmin))
		){
			res.setStatus(401);
			res.setHeader("SessionStatus","no_auth");
			res.setHeader("SessionStatusText", URLEncoder.encode("无权访问URL","UTF-8"));
			return;
		}
		/**
		 * 浏览用户不能访问配置资源
		 */
		Boolean cfgAble = UserSession.getSessionAttr(SessionKey.CFG_ABLE);
		if(requestURI.startsWith("/cfg/") && (cfgAble == null || cfgAble == false)){
			res.setStatus(401);
			res.setHeader("SessionStatus","no_auth");
			res.setHeader("SessionStatusText", URLEncoder.encode("无配置权限","UTF-8"));
			return;
		}

		filterChain.doFilter(request, response);
		
	}


}
