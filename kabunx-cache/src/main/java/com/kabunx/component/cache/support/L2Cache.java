package com.kabunx.component.cache.support;

import com.github.benmanes.caffeine.cache.Cache;
import com.kabunx.component.cache.config.L2CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class L2Cache extends AbstractValueAdaptingCache {

    private final String name;
    private final Cache<Object, Object> caffeineCache;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final String cachePrefix;

    private final String keyPrefix;

    private final Duration defaultExpiration;

    private final Duration defaultNullValuesExpiration;

    private final Map<String, Duration> expires;
    private final String topic;

    /**
     *
     */
    private AtomicBoolean l1CacheEnabled = new AtomicBoolean();

    private final Map<String, ReentrantLock> keyLockMap = new ConcurrentHashMap<>();

    public L2Cache(String name, Cache<Object, Object> caffeineCache,
                   RedisTemplate<Object, Object> redisTemplate, L2CacheConfig l2CacheConfig) {
        super(l2CacheConfig.isCacheNullValues());
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.redisTemplate = redisTemplate;

        this.cachePrefix = l2CacheConfig.getCachePrefix();
        if (StringUtils.hasText(cachePrefix)) {
            this.keyPrefix = name + ":" + cachePrefix + ":";
        } else {
            this.keyPrefix = name + ":";
        }
        this.defaultExpiration = l2CacheConfig.getRedis().getDefaultExpiration();
        this.defaultNullValuesExpiration = l2CacheConfig.getRedis().getDefaultNullValuesExpiration();
        this.expires = l2CacheConfig.getRedis().getExpires();
        this.topic = l2CacheConfig.getRedis().getTopic();
    }

    @NonNull
    @Override
    public Object getNativeCache() {
        return this;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param key         缓存的 key
     * @param valueLoader 回调，获取需要被缓存的 value
     * @param <T>         数据类型
     * @return 结果
     */
    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        // 重入锁
        ReentrantLock lock = keyLockMap.computeIfAbsent(key.toString(), s -> {
            log.trace("[L2Cache] create lock for key : {}", s);
            return new ReentrantLock();
        });
        try {
            lock.lock();
            Object value = lookup(key);
            if (Objects.nonNull(value)) {
                return (T) value;
            }
            // 如果没有缓存
            value = valueLoader.call();
            put(key, value);
            return (T) value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e.getCause());
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param key   缓存的 key
     * @param value 需要被缓存的 value
     */
    @Override
    public void put(@NonNull Object key, @Nullable Object value) {
        if (!super.isAllowNullValues() && Objects.isNull(value)) {
            log.warn("[L2Cache] the key {} value is null will be evicted", key);
            evict(key);
            return;
        }
        doPut(key, value);
    }

    @Nullable
    @Override
    public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
        Object storeValue;
        // 考虑使用分布式锁，或者将 redis 的 setIfAbsent 改为原子性操作
        synchronized (key) {
            storeValue = redisTemplate.opsForValue().get(key);
            if (storeValue == null) {
                doPut(key, value);
            }
        }
        return toValueWrapper(storeValue);
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
        sendTopic(new CacheMessage(name, key));
        caffeineCache.invalidate(key);
    }

    /**
     * 先清除 redis 中的缓存，然后清除 caffeine 中的缓存，
     * 避免短时间内如果先清除 caffeine 缓存后其他请求会再从 redis 里加载到 caffeine 中
     */
    @Override
    public void clear() {
        Set<Object> keys = redisTemplate.keys(name.concat(":"));
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        redisTemplate.delete(keys);
        // 通知
        sendTopic(new CacheMessage(name));
        // 清除本地缓存
        caffeineCache.invalidateAll();
    }

    public void clearLocal(@Nullable Object key) {
        log.debug("[L2Cache] clear local cache, the key is : {}", key);
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
    public void sendTopic(CacheMessage cacheMessage) {
        redisTemplate.convertAndSend(topic, cacheMessage);
    }

    /**
     * 先从 caffeine 中查找，如果没有从 redis 中查找
     *
     * @param key 缓存键
     * @return 缓存数据
     */
    @Nullable
    @Override
    protected Object lookup(@NonNull Object key) {
        Object cacheKey = getKey(key);
        Object value = caffeineCache.getIfPresent(key);
        if (Objects.nonNull(value)) {
            log.debug("[L2Cache] get cache from caffeine, the key is : {}", cacheKey);
            return value;
        }
        value = redisTemplate.opsForValue().get(cacheKey);
        if (Objects.nonNull(value)) {
            log.debug("[L2Cache] get cache from redis and put in caffeine, the key is : {}", cacheKey);
            caffeineCache.put(key, value);
        }
        return value;
    }

    @NonNull
    private Object getKey(Object key) {
        return keyPrefix.concat(key.toString());
    }

    private Duration getExpire(Object value) {
        Duration cacheNameExpire = expires.get(name);
        if (Objects.isNull(cacheNameExpire)) {
            cacheNameExpire = defaultExpiration;
        }
        if (Objects.isNull(value) && Objects.nonNull(defaultNullValuesExpiration)) {
            cacheNameExpire = defaultNullValuesExpiration;
        }
        return cacheNameExpire;
    }

    private void doPut(Object key, Object value) {
        value = toStoreValue(value);
        Duration expire = getExpire(value);
        // 二级缓存
        if (!expire.isNegative() && !expire.isZero()) {
            redisTemplate.opsForValue().set(getKey(key), value, expire);
        } else {
            redisTemplate.opsForValue().set(getKey(key), value);
        }
        sendTopic(new CacheMessage(this.name, key));
        // 一级缓存
        caffeineCache.put(key, value);
    }

    /**
     * 本地缓存检测
     */
    private boolean isL1Enabled() {
        return true;
    }
}
