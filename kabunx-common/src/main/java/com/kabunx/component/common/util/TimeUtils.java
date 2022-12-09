package com.kabunx.component.common.util;

import java.time.LocalDateTime;

/**
 * 自定义时间工具类
 */
public class TimeUtils {

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDateTime today(int hour, int minute, int second) {
        return LocalDateTime.now().withHour(hour).withMinute(minute).withSecond(second);
    }

    public static LocalDateTime yesterday() {
        return LocalDateTime.now().minusDays(1L);
    }

    public static LocalDateTime tomorrow() {
        return LocalDateTime.now().plusDays(1L);
    }

    public static LocalDateTime tomorrow(int hour, int minute, int second) {
        return LocalDateTime.now().plusDays(1L).withHour(hour).withMinute(minute).withSecond(second);
    }
}
