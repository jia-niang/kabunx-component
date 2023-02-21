package com.kabunx.component.cache.sync;

import com.kabunx.component.cache.L2CacheManager;
import com.kabunx.component.cache.support.CacheMessage;
import com.kabunx.component.cache.support.L2Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * 监听到
 */
@Slf4j
public class CacheMessageListener implements MessageListener {

    private final RedisTemplate<Object, Object> redisTemplate;

    private final L2CacheManager l2CacheManager;

    public CacheMessageListener(RedisTemplate<Object, Object> redisTemplate, L2CacheManager l2CacheManager) {
        this.redisTemplate = redisTemplate;
        this.l2CacheManager = l2CacheManager;
    }

    @Override
    public void onMessage(@NonNull Message message, @Nullable byte[] bytes) {
        // 消息反序列化
        CacheMessage cacheMessage = (CacheMessage) redisTemplate.getValueSerializer().deserialize(message.getBody());
        if (Objects.isNull(cacheMessage)) {
            return;
        }
        log.debug("[L2Cache] receive a redis topic message, clear local cache, the cacheName is {}, the key is {}", cacheMessage.getCacheName(), cacheMessage.getKey());
        L2Cache l2Cache = (L2Cache) l2CacheManager.getCache(cacheMessage.getCacheName());
        if (Objects.nonNull(l2Cache)) {
            l2Cache.clearLocal(cacheMessage.getKey());
        }
    }
}
