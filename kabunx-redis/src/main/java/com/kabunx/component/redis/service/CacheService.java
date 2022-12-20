package com.kabunx.component.redis.service;


import com.kabunx.component.redis.constant.enums.CacheTypeEnum;

import java.util.function.Supplier;

public interface CacheService {

    void set(String key, String value);

    void set(String key, String value, int timeout);

    void set(String key, String value, CacheTypeEnum cacheType);

    String get(String key);

    <T> T get(String key, Class<T> tClass);

    String get(String key, CacheTypeEnum cacheType);

    <T> T remember(String key, Supplier<T> supplier, int timeout, Class<T> tClass);

    <T> T forever(String key, Supplier<T> supplier, Class<T> tClass);

    void del(String key);

    void del(String key, CacheTypeEnum cacheType);
}
