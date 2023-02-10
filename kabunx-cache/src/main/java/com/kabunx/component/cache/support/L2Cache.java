package com.kabunx.component.cache.support;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class L2Cache extends AbstractValueAdaptingCache {

    private final String name;
    private final RedisTemplate<Object, Object> redisTemplate;
    private Cache<Object, Object> caffeineCache;
    private String cachePrefix;
    private final long defaultExpiration = 0;
    private Map<String, Long> expires;
    private final String topic = "cache:redis:caffeine:topic";

    public L2Cache(boolean allowNullValues, String name, RedisTemplate<Object, Object> redisTemplate) {
        super(allowNullValues);
        this.name = name;
        this.redisTemplate = redisTemplate;
    }

    @Nullable
    @Override
    protected Object lookup(@NonNull Object key) {
        Object cacheKey = getKey(key);
        Object value = caffeineCache.getIfPresent(key);
        if (Objects.nonNull(value)) {
            log.debug("get cache from caffeine, the key is : {}", cacheKey);
            return value;
        }
        value = redisTemplate.opsForValue().get(cacheKey);
        if (Objects.nonNull(value)) {
            log.debug("get cache from redis and put in caffeine, the key is : {}", cacheKey);
            caffeineCache.put(key, value);
        }
        return value;
    }

    @NonNull
    @Override
    public String getName() {
        return this.name;
    }

    @NonNull
    @Override
    public Object getNativeCache() {
        return this;
    }

    @Nullable
    @Override
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        Object value = lookup(key);
        if (Objects.nonNull(value)) {
            return (T) value;
        }
        // xx
        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lock();
            value = lookup(key);
            if (Objects.nonNull(value)) {
                return (T) value;
            }
            value = valueLoader.call();
            Object storeValue = toStoreValue(valueLoader.call());
            put(key, storeValue);
            return (T) value;
        } catch (Exception e) {
            try {
                Class<?> c = Class.forName("org.springframework.cache.Cache$ValueRetrievalException");
                Constructor<?> constructor = c.getConstructor(Object.class, Callable.class, Throwable.class);
                throw (RuntimeException) constructor.newInstance(key, valueLoader, e.getCause());
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(@NonNull Object key, @Nullable Object value) {
        if (!super.isAllowNullValues() && value == null) {
            this.evict(key);
            return;
        }
        long expire = getExpire();
        if (expire > 0) {
            redisTemplate.opsForValue().set(getKey(key), toStoreValue(value), expire, TimeUnit.MILLISECONDS);
        } else {
            redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
        }
        notice(new CacheMessage(this.name, key));
        if (Objects.nonNull(value)) {
            caffeineCache.put(key, value);
        }
    }

    /**
     * 先清除 redis 中缓存数据，然后清除 caffeine 中的缓存，
     * 避免短时间内如果先清除 caffeine 缓存后其他请求会再从 redis 里加载到 caffeine 中
     *
     * @param key the key whose mapping is to be removed from the cache
     */
    @Override
    public void evict(@NonNull Object key) {
        redisTemplate.delete(getKey(key));
        notice(new CacheMessage(this.name, key));
        caffeineCache.invalidate(key);
    }

    /**
     * 先清除 redis 中的缓存，然后清除 caffeine 中的缓存，
     * 避免短时间内如果先清除 caffeine 缓存后其他请求会再从 redis 里加载到 caffeine 中
     */
    @Override
    public void clear() {
        Set<Object> keys = redisTemplate.keys(this.name.concat(":"));
        if (Objects.isNull(keys)) {
            return;
        }
        for (Object key : keys) {
            redisTemplate.delete(key);
        }
        // 通知
        notice(new CacheMessage(this.name));
        // 清除本地缓存
        caffeineCache.invalidateAll();
    }

    @Nullable
    @Override
    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        Object cacheKey = getKey(key);
        Object prevValue;
        // 考虑使用分布式锁，或者将 redis 的 setIfAbsent 改为原子性操作
        synchronized (key) {
            prevValue = redisTemplate.opsForValue().get(cacheKey);
            if (prevValue == null) {
                long expire = getExpire();
                if (expire > 0) {
                    redisTemplate.opsForValue().set(getKey(key), toStoreValue(value), expire, TimeUnit.MILLISECONDS);
                } else {
                    redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
                }
                notice(new CacheMessage(this.name, key));
                caffeineCache.put(key, toStoreValue(value));
            }
        }
        return toValueWrapper(prevValue);
    }

    public void clearLocal(@Nullable Object key) {
        if (Objects.isNull(key)) {
            caffeineCache.invalidateAll();
        } else {
            caffeineCache.invalidate(key);
        }
    }

    /**
     * 缓存变更时通知其他节点清理本地缓存
     *
     * @param cacheMessage 缓存信息
     */
    public void notice(CacheMessage cacheMessage) {
        redisTemplate.convertAndSend(topic, cacheMessage);
    }

    @NonNull
    private Object getKey(Object key) {
        String cacheKey = StringUtils.isEmpty(cachePrefix) ? key.toString() : cachePrefix.concat(":").concat(key.toString());
        return this.name.concat(":").concat(cacheKey);
    }

    private Long getExpire() {
        Long cacheNameExpire = expires.get(this.name);
        return cacheNameExpire == null ? defaultExpiration : cacheNameExpire;
    }
}
