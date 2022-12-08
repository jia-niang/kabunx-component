package com.kabunx.component.redis.service.impl;

import com.kabunx.component.core.exception.BizException;
import com.kabunx.component.redis.LuaScriptHolder;
import com.kabunx.component.redis.service.LockService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LockServiceImpl implements LockService {
    public static final Long LOCKED = 1L;
    public static final Long UNLOCKED = 1L;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisScript<Long> lockScript;
    private final RedisScript<Long> unLockScript;


    public LockServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        lockScript = LuaScriptHolder.getLockScript();
        unLockScript = LuaScriptHolder.getUnlockScript();
    }

    /**
     * 有返回值的抢夺锁
     *
     * @param ttl 超时时间
     */
    public boolean lock(String key, Long ttl) {
        if (Objects.isNull(key)) {
            return false;
        }
        try {
            List<String> redisKeys = new ArrayList<>();
            redisKeys.add(key);
            redisKeys.add(String.valueOf(ttl));
            Long result = stringRedisTemplate.execute(lockScript, redisKeys);
            return Objects.nonNull(result) && result.equals(LOCKED);
        } catch (Exception e) {
            throw new BizException("抢锁失败");
        }
    }

    // 释放锁
    public boolean unlock(String key) {
        if (Objects.isNull(key)) {
            return false;
        }
        try {
            List<String> redisKeys = new ArrayList<>();
            redisKeys.add(key);
            Long result = stringRedisTemplate.execute(unLockScript, redisKeys);
            return Objects.nonNull(result) && result.equals(UNLOCKED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("释放锁失败");
        }
    }
}
