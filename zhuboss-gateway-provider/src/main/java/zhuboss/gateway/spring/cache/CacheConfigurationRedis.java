package zhuboss.gateway.spring.cache;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration("cacheConfigurationRedis")
@ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = false)
public class CacheConfigurationRedis extends CachingConfigurerSupport {

    //redis 缓存默认过期时间单位：秒
    private static final Long DEFAULT_EXPIRATION_TIME = 600L;


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // 默认没有特殊指定的
                .computePrefixWith(cacheName -> "caching:" + cacheName);

        // 针对不同cacheName，设置不同的过期时间
        Map<String, RedisCacheConfiguration> initialCacheConfiguration = new HashMap<String, RedisCacheConfiguration>() {{
            for(CacheConstants cacheConstants : CacheConstants.values()){
                put(cacheConstants.getValue(), RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(cacheConstants.getExpires()))); // 过期时间秒
            }
            // ...
        }};

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig) // 默认配置（强烈建议配置上）。  比如动态创建出来的都会走此默认配置
                .withInitialCacheConfigurations(initialCacheConfiguration) // 不同cache的个性化配置
                .build();
        return redisCacheManager;
    }

    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("testRedisCache", this.getRedisCacheConfigurationWithTtl(3000L));
        redisCacheConfigurationMap.put("UserInfoListAnother", this.getRedisCacheConfigurationWithTtl(18000L));
        return redisCacheConfigurationMap;
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Long seconds) {
        // 设置CacheManager的值序列化方式为JdkSerializationRedisSerializer,
        // 但其实RedisCacheConfiguration默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value
        ClassLoader loader = this.getClass().getClassLoader();
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader);
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(pair).entryTtl(Duration.ofSeconds(seconds));
        return redisCacheConfiguration;
    }
}