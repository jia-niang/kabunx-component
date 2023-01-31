package com.kabunx.component.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.function.Supplier;

@Slf4j
public class WatchUtils {

    public static <T> T supplier(String key, String taskName, Supplier<T> supplier) {
        final StopWatch stopWatch = new StopWatch(key);
        stopWatch.start(taskName);
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("执行任务发生异常", e);
            return null;
        } finally {
            stopWatch.stop();
            log.info(stopWatch.toString());
        }
    }

}
