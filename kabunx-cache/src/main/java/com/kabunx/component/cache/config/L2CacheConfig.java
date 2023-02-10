package com.kabunx.component.cache.config;

import java.util.HashSet;
import java.util.Set;

public class L2CacheConfig {

    /**
     * 是否存储空值，设置为true时，可防止缓存穿透
     */
    private boolean allowNullValues = true;

    /**
     * 是否动态根据cacheName创建Cache的实现，默认true
     */
    private boolean dynamic = true;


    private Set<String> cacheNames = new HashSet<>();
}
