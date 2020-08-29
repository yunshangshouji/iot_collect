package zhuboss.gateway.spring.web.filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class FilterConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.redis.shiro.host", matchIfMissing = false)
    public FilterRegistrationBean<Filter> delegatingFilterProxy(@Qualifier("shiroFilter") Filter shiroFilter){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(1);
	    /*
	     * //以下面这种方式，每次都会读取redis
	     * DelegatingFilterProxy proxy = new DelegatingFilterProxy();
	    proxy.setTargetFilterLifecycle(true);
	    proxy.setTargetBeanName("shiroFilter");
	    filterRegistrationBean.setFilter(proxy);*/
        filterRegistrationBean.setFilter(shiroFilter);
        filterRegistrationBean.addUrlPatterns(new String[]{"/*"});
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<OpenIdFilter> buildOpenIdFilter() {
        FilterRegistrationBean<OpenIdFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.setFilter(new OpenIdFilter());
        filterRegistrationBean.setName(OpenIdFilter.class.getName());
        filterRegistrationBean.addUrlPatterns("/*" );
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<LoginFilter> buildLoginFilter() {
        FilterRegistrationBean<LoginFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(3);
        filterRegistrationBean.setFilter(new LoginFilter());
        filterRegistrationBean.setName(LoginFilter.class.getName());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }


}
