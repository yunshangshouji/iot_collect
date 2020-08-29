package zhuboss.gateway;


import com.alibaba.dubbo.spring.boot.DubboAutoConfiguration;
import com.alibaba.dubbo.spring.boot.DubboCommonAutoConfiguration;
import com.alibaba.dubbo.spring.boot.DubboProviderAutoConfiguration;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import zhuboss.framework.spring.SpringConfigLocationUtil;

@SpringBootApplication(
        //关闭自动配置,就是把 META-INF/spring.factories 文件不需要自动配置的类过滤掉
)
@Import({
        //系统属性
        PropertyPlaceholderAutoConfiguration.class,
        //web容器
        ServletWebServerFactoryAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        //database
	DataSourceAutoConfiguration.class,
        TransactionAutoConfiguration.class,
        //mvc

        DispatcherServletAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class,
//	HttpMessageConvertersAutoConfiguration.class,
        //freemarker
        FreeMarkerAutoConfiguration.class,
        // ActiveMQ
        JmsAutoConfiguration.class,
        //Dubbo
        DubboAutoConfiguration.class,
        DubboCommonAutoConfiguration.class,
        DubboProviderAutoConfiguration.class,

        RedisAutoConfiguration.class,

        //邮件发送
        MailSenderAutoConfiguration.class

})
@ComponentScan(basePackages={"zhuboss.framework", "zhuboss.gateway"})
@EnableCaching
@EnableScheduling
@EnableDubboConfiguration
public class StartTx {
    public static void main(String[] args) throws Exception {
//		LogbackUtil.load("./conf/logback.xml");
        SpringConfigLocationUtil.resetConfig();

        //启动程序
        SpringApplication.run(StartTx.class, args);

    }
}
