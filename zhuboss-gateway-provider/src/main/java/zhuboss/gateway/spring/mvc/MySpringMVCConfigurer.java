package zhuboss.gateway.spring.mvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootConfiguration
public class MySpringMVCConfigurer extends WebMvcConfigurerAdapter{

//	@Value("${static.path}")
//	private String staticPath;

	/**
	 * WebMvcAutoConfiguration只支持配置一个static path，但实际spring是支持多个的！
	 */
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/static2/**").addResourceLocations(staticPath);
//	}

	/**
	 * 默认首页跳到/login
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:/static/app_list.html");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}


	/**
	 * 目的是配置 SpringMVC DispatcherServlet 的 url-pattern
	 * @param dispatcherServlet
	 * @return
	 */
//	@Bean
//	public ServletRegistrationBean dispatcherServletRegistration(DispatcherServlet dispatcherServlet) {
//	    ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet, UrlConstants.SPRING_MVC_URL_PATTERN);
//	    registration.setName("dispatcherServlet");
//	    return registration;
//	}

	
}
