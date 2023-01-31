package com.kabunx.component.common.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数工具类
 */
public class RandomUtils {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 按照范围，生成随机的整数
     *
     * @param max 最大值
     * @return [1, max]之间的整数
     */
    public static int lteMax(int max) {
        return Math.abs(ThreadLocalRandom.current().nextInt(max)) + 1;
    }

    /**
     * 按照上下限范围，生成随机的整数
     *
     * @param min 下限
     * @param max 上限
     * @return [min, max]之间的整数
     */
    public static int range(int min, int max) {
        return lteMax(max - min) + min;
    }
}
