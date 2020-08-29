package zhuboss.gateway.spring.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(CacheConfigurationRedis.class) //如果启用redis就不用ehcache
public class CacheConfigurationEhcache extends CachingConfigurerSupport {

    /*
     * ehcache 主要的管理器
     */
    @Bean(name = "appEhCacheCacheManager")
    public EhCacheCacheManager ehCacheCacheManager(){
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean ();
        cacheManagerFactoryBean.afterPropertiesSet();
        CacheManager cacheManager = cacheManagerFactoryBean.getObject ();
        for(CacheConstants cacheConstants : CacheConstants.values()){
            CacheConfiguration configuration = new CacheConfiguration(cacheConstants.getValue(),1000);
            configuration.setTimeToIdleSeconds(cacheConstants.getExpires());
            cacheManager.addCache(new Cache(configuration));
        }
        return new EhCacheCacheManager (cacheManager);
    }

}