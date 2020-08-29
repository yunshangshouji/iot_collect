package zhuboss.gateway.spring.web.freemarker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import freemarker.template.Configuration;

@org.springframework.context.annotation.Configuration
public class FreemarkerConfig {
	 

	  @Bean
	  public Configuration getConfiguration() {
		  Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);;
		  return configuration;
	  }
	  
//	  @PostConstruct
//	  public void setSharedVariable(Configuration configuration) throws TemplateModelException {
//		 /**
//		  * FreeMarker 共享变量
//		  */
//	  }
	 

}
