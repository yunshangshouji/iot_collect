package zhuboss.gateway.api;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import zhuboss.framework.swagger.SwaggerMyController;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(name="api.open",havingValue = "true")
public class SwaggerAPI {
    @Bean
    public Docket api() throws FileNotFoundException, JAXBException {
        ApiInfo apiInfo = new ApiInfo("工业组态服务", "适用于HttpRest、DUBBO，调用方式不一样，但参数一致。" +
                "<br>Http请求头Content-Type:application/json" +
                "<br>关联ID指记录在应用系统中的唯一ID" +
                "<br>同步操作：根据关联ID自动执行insert或update操作", "1.0", null, null, null, null, new ArrayList<>());
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/api/.*"))
                .build();
    }

    @Bean
    SwaggerMyController getSwaggerMyController(Environment environment, DocumentationCache documentationCache, ServiceModelToSwagger2Mapper mapper){
        return new SwaggerMyController(environment,documentationCache,mapper);
    }

}
