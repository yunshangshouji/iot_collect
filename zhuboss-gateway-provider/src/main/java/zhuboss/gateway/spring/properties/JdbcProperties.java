package zhuboss.gateway.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix="jdbc",ignoreUnknownFields = true)
public class JdbcProperties {
	private String driver;
	private String url;
	private String username;
	private String password;
	private Integer init;
	private Integer minIdle;
	private Integer maxActive;
	private String testSql;
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getInit() {
		return init;
	}
	public void setInit(Integer init) {
		this.init = init;
	}
	public Integer getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}
	public Integer getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}
	public String getTestSql() {
		return testSql;
	}
	public void setTestSql(String testSql) {
		this.testSql = testSql;
	}
	
}
