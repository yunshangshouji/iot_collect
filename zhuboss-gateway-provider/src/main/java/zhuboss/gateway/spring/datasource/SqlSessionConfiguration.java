package zhuboss.gateway.spring.datasource;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import zhuboss.gateway.spring.properties.JdbcProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableConfigurationProperties(JdbcProperties.class)
@MapperScan(value = "zhuboss.gateway.**.mapper")
@EnableTransactionManagement
/*
@Component
@AutoConfigureAfter(SqlSessionConfiguration.class)
public class MyBatisMapperScannerConfig {
	@Bean(name="mapperScannerConfigurer")
	public MapperScannerConfigurer getMapperScannerConfigurer() {
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		mapperScannerConfigurer.setMarkerInterface(AutoMapper.class);
		mapperScannerConfigurer.setBasePackage("com.keasi.**.mapper"); //common,waterpump
		return mapperScannerConfigurer;
	}
}
*/
public class SqlSessionConfiguration {
	static Logger logger = LoggerFactory.getLogger(SqlSessionConfiguration.class);

	private final JdbcProperties jdbcProperties;
	public SqlSessionConfiguration(JdbcProperties jdbcProperties) {
		this.jdbcProperties = jdbcProperties;
	}

	@Bean(name="dataSource",initMethod="init",destroyMethod="close")
    public DataSource getDataSource() throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(jdbcProperties.getDriver());
		dataSource.setUrl(jdbcProperties.getUrl());
		dataSource.setUsername(jdbcProperties.getUsername());
		dataSource.setPassword(jdbcProperties.getPassword());
		dataSource.setInitialSize(jdbcProperties.getInit());
		dataSource.setMinIdle(jdbcProperties.getMinIdle());
		dataSource.setMaxActive(jdbcProperties.getMaxActive());
		dataSource.setMaxWait(60000);
		dataSource.setTimeBetweenEvictionRunsMillis(60000); //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		dataSource.setMinEvictableIdleTimeMillis(300000); //配置一个连接在池中最小生存的时间，单位是毫秒
		dataSource.setValidationQuery(jdbcProperties.getTestSql());
		dataSource.setFilters("stat");
		return dataSource;
	}
	
	@Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
	
	
	@Bean(name="sqlSessionFactory")
	public SqlSessionFactory getSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		try {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] rs = resolver.getResources("classpath*:mappers/*.xml"); //"/mappers/**/*.xml"
			for(Resource resource : rs){
				logger.info(">>>>>>>>>>>>>>>>"+resource.getDescription());
			}
			sessionFactory.setMapperLocations(rs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		try {
			return sessionFactory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	
}
