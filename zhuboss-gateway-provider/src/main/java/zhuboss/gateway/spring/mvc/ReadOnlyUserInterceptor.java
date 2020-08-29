package zhuboss.gateway.spring.mvc;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import zhuboss.framework.bean.JsonResponse;
import zhuboss.gateway.spring.web.filter.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class ReadOnlyUserInterceptor implements HandlerInterceptor {
    public static final String READ_ONLY_JSON = JSON.toJSONString(new JsonResponse<>(false,"当前登录为“演示用户”，不能执行修改操作")).toString();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //非spring mvc method、未登录、登录非演示用户
        if(! (handler instanceof HandlerMethod) || UserSession.getUserId() == null || UserSession.getUserId()!=0){
            return true;
        }

        Method method = ((HandlerMethod)handler).getMethod();
        if(method.getAnnotation(WriteAction.class)!=null){
            response.setHeader("Content-Type","application/json;charset=UTF-8");
            response.getOutputStream().write(READ_ONLY_JSON.getBytes());
            response.getOutputStream().flush();
            return false;
        }

        return true;
    }

}
