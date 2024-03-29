package com.kabunx.component.redis;

import com.kabunx.component.common.util.IOUtils;
import com.kabunx.component.redis.service.impl.LockServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Objects;

/**
 * Lua脚本
 */
@Slf4j
public class LuaScriptHolder {
    /**
     * 秒杀令牌操作的脚本
     */
    private static final String seckillLua = "lua/seckill.lua";

    public static RedisScript<Long> seckillScript = null;

    public static synchronized RedisScript<Long> getSeckillScript() {
        if (Objects.isNull(seckillScript)) {
            seckillScript = getDefaultRedisScript(seckillLua);
        }
        return seckillScript;
    }


    private static final String lockLua = "lua/lock.lua";
    private static RedisScript<Long> lockScript = null;

    public static synchronized RedisScript<Long> getLockScript() {
        if (Objects.isNull(lockScript)) {
            lockScript = getDefaultRedisScript(lockLua);
        }
        return lockScript;
    }

    private static final String unLockLua = "lua/unlock.lua";
    static RedisScript<Long> unLockScript = null;

    public static synchronized RedisScript<Long> getUnlockScript() {
        if (Objects.isNull(unLockScript)) {
            unLockScript = getDefaultRedisScript(unLockLua);
        }
        return unLockScript;
    }

    //lua 脚本的类路径
    private static final String rateLimitLua = "lua/rate_limiter.lua";
    private static RedisScript<Long> rateLimiterScript = null;

    public static synchronized RedisScript<Long> getRateLimitScript() {
        if (Objects.isNull(rateLimiterScript)) {
            rateLimiterScript = getDefaultRedisScript(rateLimitLua);
        }
        return rateLimiterScript;
    }

    private static RedisScript<Long> getDefaultRedisScript(String name) {
        String script = IOUtils.loadJarFile(LockServiceImpl.class.getClassLoader(), name);
        if (StringUtils.isEmpty(script)) {
            log.error("[LuaScript] lua script load failed - {}", name);
            return null;
        } else {
            return new DefaultRedisScript<>(script, Long.class);
        }
    }
}
