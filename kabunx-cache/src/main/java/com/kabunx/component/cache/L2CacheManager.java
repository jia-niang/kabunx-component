package com.kabunx.component.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.kabunx.component.cache.config.L2CacheConfig;
import com.kabunx.component.cache.support.L2Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;


@Slf4j
public class L2CacheManager implements CacheManager {

    private final L2CacheConfig l2CacheConfig;

    private final RedisTemplate<Object, Object> redisTemplate;

    private boolean dynamic = true;

    private final Set<String> cacheNames;

    private Object serverId;

    private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public L2CacheManager(RedisTemplate<Object, Object> redisTemplate, L2CacheConfig l2CacheConfig) {
        this.redisTemplate = redisTemplate;
        this.l2CacheConfig = l2CacheConfig;

        this.dynamic = l2CacheConfig.isDynamic();
        this.cacheNames = l2CacheConfig.getCacheNames();
        this.serverId = l2CacheConfig.getServerId();
    }

    @Nullable
    @Override
    public Cache getCache(@NonNull String name) {
        Cache cache = cacheMap.get(name);
        if (Objects.nonNull(cache)) {
            return cache;
        }
        if (!dynamic && !cacheNames.contains(name)) {
            return null;
        }
        cache = new L2Cache(name, getCaffeineCache(), redisTemplate, l2CacheConfig);
        Cache oldCache = cacheMap.putIfAbsent(name, cache);
        log.debug("[L2Cache] create cache instance, the cache name is : {}", name);
        return Objects.isNull(oldCache) ? cache : oldCache;
    }

    @NonNull
    @Override
    public Collection<String> getCacheNames() {
        return cacheNames;
    }

    public com.github.benmanes.caffeine.cache.Cache<Object, Object> getCaffeineCache() {
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
        doIfPresent(l2CacheConfig.getCaffeine().getExpireAfterAccess(), cacheBuilder::expireAfterAccess);
        doIfPresent(l2CacheConfig.getCaffeine().getExpireAfterWrite(), cacheBuilder::expireAfterWrite);
        doIfPresent(l2CacheConfig.getCaffeine().getRefreshAfterWrite(), cacheBuilder::refreshAfterWrite);
        if (l2CacheConfig.getCaffeine().getInitialCapacity() > 0) {
            cacheBuilder.initialCapacity(l2CacheConfig.getCaffeine().getInitialCapacity());
        }
        if (l2CacheConfig.getCaffeine().getMaximumSize() > 0) {
            cacheBuilder.maximumSize(l2CacheConfig.getCaffeine().getMaximumSize());
        }
        return cacheBuilder.build();
    }


    /**
     * 简单判断 duration 是否合法，如果合法执行 consumer
     *
     * @param duration the duration
     * @param consumer 回调
     */
    private static void doIfPresent(Duration duration, Consumer<Duration> consumer) {
        if (Objects.nonNull(duration) && !duration.isNegative()) {
            consumer.accept(duration);
        }
    }

}
