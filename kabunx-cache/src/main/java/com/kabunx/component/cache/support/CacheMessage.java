package com.kabunx.component.cache.support;

import lombok.Data;

import java.io.Serializable;

@Data
public class CacheMessage implements Serializable {
    private static final long serialVersionUID = -3574999310442078193L;
    private String cacheName;

    /**
     * 标识更新或删除操作
     */
    private CacheMessageType cacheMessageType;
    private Object key;

    private Object value;

    /**
     * 源主机标识，用来避免重复操作
     */
    private String messageSource;

    public CacheMessage(String cacheName) {
        this.cacheName = cacheName;
    }

    public CacheMessage(String cacheName, Object key) {
        this.cacheName = cacheName;
        this.key = key;
    }


}
