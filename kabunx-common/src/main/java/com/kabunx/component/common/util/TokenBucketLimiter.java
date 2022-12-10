package com.kabunx.component.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 令牌桶工具类
 */
@Slf4j
public class TokenBucketLimiter {
    // 上一次令牌发放时间
    public long lastTime = System.currentTimeMillis();
    // 桶的容量
    public int capacity = 2;
    // 令牌生成速度 /s
    public int rate = 2;
    // 当前令牌数量
    public int tokens;

    /**
     * @param applyCount 申请总数
     * @return false 没有被限制到,true 被限流
     */
    public boolean tryAcquire(int applyCount) {
        long now = System.currentTimeMillis();
        // 时间间隔,单位为 ms
        long gap = now - lastTime;
        // 当前令牌数
        tokens = Math.min(capacity, (int) (tokens + gap * rate / 1000));
        log.info("tokens {} capacity {} gap {} ", tokens, capacity, gap);

        if (tokens < applyCount) {
            // 若拿不到令牌,则拒绝
            return true;
        }
        // 还有令牌，领取令牌
        tokens -= applyCount;
        lastTime = now;
        // log.info("剩余令牌.." + tokens);
        return false;
    }
}
