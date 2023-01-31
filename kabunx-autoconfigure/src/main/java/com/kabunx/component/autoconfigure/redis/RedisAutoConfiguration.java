package com.kabunx.component.autoconfigure.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.kabunx.component.redis.service.CacheService;
import com.kabunx.component.redis.service.LockService;
import com.kabunx.component.redis.service.RedisService;
import com.kabunx.component.redis.service.impl.CacheServiceImpl;
import com.kabunx.component.redis.service.impl.LockServiceImpl;
import com.kabunx.component.redis.service.impl.RedisServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedisSerializer.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = jsonRedisSerializer();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Primary
    @Bean("hotCache")
    @ConditionalOnBean(RedisConnectionFactory.class)
    CacheManager hotCacheManager(RedisConnectionFactory factory) {
        // 配置序列化
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("cache:hot:")
                .entryTtl(Duration.ofMinutes(5L))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
                .cacheDefaults(configuration)
                .build();
    }

    @Bean("lazyCache")
    @ConditionalOnBean(RedisConnectionFactory.class)
    CacheManager lazyCacheManager(RedisConnectionFactory factory) {
        // 配置序列化
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith("cache:lazy:")
                .entryTtl(Duration.ofDays(7L))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
                .cacheDefaults(configuration)
                .build();
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    @ConditionalOnClass(RedisServiceImpl.class)
    RedisService redisService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisServiceImpl(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnClass(CacheServiceImpl.class)
    CacheService cacheService(StringRedisTemplate stringRedisTemplate) {
        return new CacheServiceImpl(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnClass(LockServiceImpl.class)
    LockService lockService(StringRedisTemplate stringRedisTemplate) {
        return new LockServiceImpl(stringRedisTemplate);
    }

    private Jackson2JsonRedisSerializer<Object> jsonRedisSerializer() {
        // 创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 必须设置，否则无法将JSON转化为对象，会转化成Map类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

}
