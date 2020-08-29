package zhuboss.gateway.spring.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.util.StringUtils;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.framework.mybatis.query.QueryClauseBuilder;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.gateway.mapper.UserPOMapper;
import zhuboss.gateway.po.UserPO;
import zhuboss.gateway.spring.shiro.ShiroConfig;
import zhuboss.gateway.util.HttpClientUtil;
import zhuboss.gateway.wx.wx.WxConfig;

import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

@Slf4j
public class OpenIdFilter implements Filter {

	private static ThreadLocal<String> openIdThreadLocal = new ThreadLocal<>();

	public static String getOpenId(){
		return openIdThreadLocal.get();
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	private void handleOpenId(String openId) throws IOException, ServletException {
		UserSession.setSessionAttr(SessionKey.OPEN_ID, openId);
		//根据openid设置session
		UserPOMapper userPOMapper = SpringContextUtils.getBean(UserPOMapper.class);
		UserPO userPO = userPOMapper.selectOneByClause(new QueryClauseBuilder().andEqual(UserPO.Fields.OPENID, openId));
		if (userPO != null) {
			if (ShiroConfig.enable()) {
				//登录验证
				UsernamePasswordToken token = new UsernamePasswordToken(openId, "");
				try {
					SecurityUtils.getSubject().login(token);
				} catch (AuthenticationException e) {
					log.error("登录失败", e);
				}
			}
			UserSession.setSessionAttr(SessionKey.USER_ID, userPO.getId());
			//
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		/**
		 * Session中有openid
		 */
		String openId = UserSession.getSessionAttr(SessionKey.OPEN_ID);
		if(StringUtils.hasText(openId)){
			if(UserSession.getUserId() == null){
				handleOpenId(openId);
			}
			chain.doFilter(request,response);
			return;
		}

		/**
		 * 用于测试
		 */
		if(request.getParameter("_openid")!=null){
			openId = request.getParameter("_openid");
			handleOpenId(openId);
			chain.doFilter(request,response);
			return;
		}

		/**
		 * 来自微信回调
		 */

		String code = request.getParameter("code");
		String state = request.getParameter("state");
		if(StringUtils.hasText(code) && StringUtils.hasText(state)) {//redirect_uri/?code=CODE&state=STATE ， 来自于微信转发
			WxConfig wxConfig = SpringContextUtils.getBean(WxConfig.class);
			String returnText = getAccessToken(wxConfig.getAppId(),wxConfig.getSecret(),code);
			JSONObject jsonObject = JSON.parseObject(returnText);
			if(StringUtils.hasText(jsonObject.getString("errcode"))){
				log.error(returnText);
				response.getOutputStream().write(returnText.getBytes());
				response.getOutputStream().flush();
				return;
			}
			openId = jsonObject.getString("openid");
			handleOpenId(openId);
			chain.doFilter(request,response);
			return;
		}
		/**
		 * 配置的菜单链接(需要open id)
		 */
		else if(request.getParameter("_wx") != null ){ //
			String requestURI = ((HttpServletRequest)request).getRequestURI();
			WxConfig wxConfig = SpringContextUtils.getBean(WxConfig.class);
			String  WEI_SITE_DOMAIN = wxConfig.getDomain();
			String redirectURL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+wxConfig.getAppId()+"&redirect_uri=" + URLEncoder.encode(WEI_SITE_DOMAIN+requestURI,"UTF-8")+/*URLEncoder.encode(WEI_SITE_DOMAIN+requestURI)+*/"&response_type=code&scope=snsapi_base&state="+new Date().getTime()+Math.random()+"#wechat_redirect";
			HttpServletResponse res = (HttpServletResponse)response;
			res.sendRedirect(redirectURL);
			return;
		}

		/**
		 * 其它正常请求
		 */
		chain.doFilter(request,response);
			
		
	}

	private String getAccessToken(String appId,String secret,String code){
		String access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
		HttpGet get = new HttpGet(access_token_url  );
		try {
			HttpResponse response = SpringContextUtils.getBean(HttpClientUtil.class).getHttpClient().execute(get);
			if(response.getEntity() == null) return null;
			String responseText = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			return responseText;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
