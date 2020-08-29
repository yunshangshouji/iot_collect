package zhuboss.gateway.spring.web;

import org.apache.tomcat.websocket.server.Constants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class WebAppRootContext implements ServletContextInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(WebAppRootListener.class);
        //配置WebSocket sendtext() 支持的最大字节数
        servletContext.setInitParameter(Constants.TEXT_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM,//这里是注入参数的名称
                "5242800"); //5M
        //配置WebSocket sendBinaryBytes() 支持的二进制字节数
        servletContext.setInitParameter(Constants.BINARY_BUFFER_SIZE_SERVLET_CONTEXT_INIT_PARAM,//这里是注入参数的名称
                "10485760"); //10M
    }
}
