package com.kabunx.component.redis.service.impl;

import com.kabunx.component.common.util.JsonUtils;
import com.kabunx.component.redis.constant.enums.CacheTypeEnum;
import com.kabunx.component.redis.service.CacheService;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存操作
 */
public class CacheServiceImpl implements CacheService {
    private final StringRedisTemplate stringRedisTemplate;

    public CacheServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, int timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    // 增加缓存
    @Override
    public void set(String key, String value, CacheTypeEnum cacheType) {
        set(cacheType.getPrefix() + key, value, cacheType.getSeconds());
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public <T> T get(String key, Class<T> tClass) {
        String json = get(key);
        return JsonUtils.json2Object(json, tClass);
    }

    @Override
    public String get(String key, CacheTypeEnum cacheType) {
        return stringRedisTemplate.opsForValue().get(cacheType.getPrefix() + key);
    }

    @Override
    public <T> T remember(String key, Supplier<T> supplier, int timeout, Class<T> tClass) {
        T data = get(key, tClass);
        if (Objects.nonNull(data)) {
            return data;
        }
        T result = supplier.get();
        set(key, JsonUtils.object2Json(result), timeout);
        return result;
    }

    @Override
    public <T> T forever(String key, Supplier<T> supplier, Class<T> tClass) {
        T data = get(key, tClass);
        if (Objects.nonNull(data)) {
            return data;
        }
        T result = supplier.get();
        set(key, JsonUtils.object2Json(result));
        return result;
    }

    @Override
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public void del(String key, CacheTypeEnum cacheType) {
        del(cacheType.getPrefix() + key);
    }
}
