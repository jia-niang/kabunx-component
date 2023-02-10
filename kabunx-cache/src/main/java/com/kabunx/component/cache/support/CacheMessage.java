package com.kabunx.component.cache.support;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheMessage implements Serializable {
    private static final long serialVersionUID = 5987219310442078193L;
    private String cacheName;
    private Object key;

    public CacheMessage(String cacheName) {
        this.cacheName = cacheName;
    }

    public CacheMessage(String cacheName, Object key) {
        this.cacheName = cacheName;
        this.key = key;
    }
}
