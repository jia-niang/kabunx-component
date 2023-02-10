package com.kabunx.component.cache;

import com.kabunx.component.cache.config.L2CacheConfig;
import com.kabunx.component.cache.support.L2Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Slf4j
public class L2CacheManager implements CacheManager {

    private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();


    private RedisTemplate<Object, Object> redisTemplate;

    private L2CacheConfig l2CacheConfig;

    private boolean dynamic = true;

    private Set<String> cacheNames;

    @Nullable
    @Override
    public Cache getCache(@NonNull String name) {
        Cache cache = cacheMap.get(name);
        if (Objects.nonNull(cache)) {
            return cache;
        }
        if (!dynamic && !cacheNames.contains(name)) {
            return cache;
        }
        cache = new L2Cache(true, name, redisTemplate);
        Cache oldCache = cacheMap.putIfAbsent(name, cache);
        log.debug("create cache instance, the cache name is : {}", name);
        return oldCache == null ? cache : oldCache;
    }

    @NonNull
    @Override
    public Collection<String> getCacheNames() {
        return null;
    }
}
