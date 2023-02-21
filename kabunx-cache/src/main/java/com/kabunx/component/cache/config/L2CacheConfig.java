package com.kabunx.component.cache.config;

import lombok.Data;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class L2CacheConfig {

    /**
     * 当前节点 id。来自当前节点的缓存更新通知不会被处理
     */
    private Object serverId;

    private Set<String> cacheNames = new HashSet<>();

    /**
     * 是否存储空值，默认 true，防止缓存穿透
     */
    private boolean cacheNullValues = true;

    /**
     * 是否动态根据 cacheName 创建 Cache 的实现，默认 true
     */
    private boolean dynamic = true;

    /**
     * 缓存 key 的前缀
     */
    private String cachePrefix;

    private Caffeine caffeine;

    private Redis redis;


    @Data
    public static class Caffeine {
        /**
         * 是否全部启用一级缓存，默认 false
         */
        private boolean enabled = false;

        /**
         * 访问后过期时间
         */
        private Duration expireAfterAccess;

        /**
         * 写入后过期时间
         */
        private Duration expireAfterWrite;

        /**
         * 写入后刷新时间
         */
        private Duration refreshAfterWrite;

        /**
         * 初始化大小
         */
        private int initialCapacity;

        /**
         * 最大缓存对象个数，超过此数量时之前放入的缓存将失效
         */
        private long maximumSize;

        /**
         * 手动配置走一级缓存的缓存名字集合，针对 cacheName 维度
         */
        private Set<String> cacheNameSet = new HashSet<>();
    }


    @Data
    public static class Redis {
        /**
         * 全局过期时间，默认不过期
         */
        private Duration defaultExpiration = Duration.ZERO;

        /**
         * 全局空值过期时间，默认和有值的过期时间一致，一般设置空值过期时间较短
         */
        private Duration defaultNullValuesExpiration = null;

        /**
         * 每个 cacheName 的过期时间，优先级比 defaultExpiration 高
         */
        private Map<String, Duration> expires = new HashMap<>();

        /**
         * 缓存更新时通知其他节点的 topic 名称
         */
        private String topic = "cache:redis:caffeine:topic";

        /**
         * 生成当前节点 id 的 key，当配置了 spring.cache.l2.server-id 时，该配置不生效
         */
        private String serverIdGeneratorKey = "cache:redis:caffeine:server-id-sequence";
    }
}
