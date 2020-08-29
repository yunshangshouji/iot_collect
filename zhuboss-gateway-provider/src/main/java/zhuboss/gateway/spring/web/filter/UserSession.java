package zhuboss.gateway.spring.web.filter;

import org.apache.shiro.session.Session;
import zhuboss.framework.shiro.SessionUtil;
import zhuboss.gateway.spring.shiro.ShiroConfig;

import javax.servlet.http.HttpServletRequest;

public class UserSession {
    public static HttpServletRequest request;

    public static void setSessionAttr(SessionKey key, Object value) {
        if(ShiroConfig.enable()){
            Session session = SessionUtil.getSubject().getSession(true);
            session.setAttribute(key, value);
        }else{
            request.getSession().setAttribute(key.name(),value);
        }
    }

    public static <T> T getSessionAttr(SessionKey key) {
        if(ShiroConfig.enable()){
            Session session = SessionUtil.getSubject().getSession(false);
            return session == null ? null : (T)session.getAttribute(key);
        }else{
            return (T)request.getSession().getAttribute(key.name());
        }
    }

    public static Integer getAppId(){
        return getSessionAttr(SessionKey.APP_ID);
    }

    public static Integer getUserId(){
        if(ShiroConfig.enable()){
            return getSessionAttr(SessionKey.USER_ID);
        }else{
            return (Integer) request.getSession().getAttribute(SessionKey.USER_ID.name());
        }
    }

    public static Boolean isAdmin(){
        return getSessionAttr(SessionKey.IS_ADMIN);
    }

}
