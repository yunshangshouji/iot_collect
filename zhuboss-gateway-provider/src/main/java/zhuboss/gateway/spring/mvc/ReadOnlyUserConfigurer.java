package zhuboss.gateway.spring.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ReadOnlyUserConfigurer  implements WebMvcConfigurer {
    @Autowired
    ReadOnlyUserInterceptor readOnlyUserInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptor = registry.addInterceptor(readOnlyUserInterceptor);
        /**
         * 添加拦截的路径
         * /为根路径
         * /*为一级路径
         * /** 为所有路径包括多级
         */
        interceptor.addPathPatterns("/**");

    }
}
