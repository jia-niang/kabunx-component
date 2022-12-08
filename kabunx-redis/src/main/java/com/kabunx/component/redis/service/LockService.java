package com.kabunx.component.redis.service;

public interface LockService {

    boolean lock(String key, Long ttl);

    boolean unlock(String key);
}
