package com.project.library.caching;

//import io.lettuce.core.dynamic.annotation.Value;
import com.project.library.books.Book;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Log4j2
@Configuration
@ConditionalOnProperty(value="resolved.cache.enabled", havingValue = "true", matchIfMissing = true)
public class CacheConfiguration {
    @Value("${resolved.cache.ttl:30}")
    private long ttlInMinutes;

    @Value("${ENV:env}")
    private String environment;

    private static final String API_PREFIX = "v1";
    private static final String SEPARATOR = ":";


    @Bean(value = "cacheManager")
    public CacheManager redisCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        log.info("[Cache] injecting TTL cache: enabled");
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(ttlInMinutes))
                .computePrefixWith(cacheName -> API_PREFIX.concat(SEPARATOR).concat(environment).concat(SEPARATOR)
                        .concat(cacheName).concat(SEPARATOR)) // cache key prefix with the environment
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
        redisCacheConfiguration.usePrefix();

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(redisCacheConfiguration).build();
    }
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    //Creating RedisTemplate for Entity 'Book'
    @Bean
    public RedisTemplate<String, Book> redisTemplate(){
        RedisTemplate<String, Book> bookTemplate = new RedisTemplate<>();
        bookTemplate.setConnectionFactory(redisConnectionFactory());
        return bookTemplate;
    }
}
