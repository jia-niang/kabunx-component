package com.kabunx.component.log.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * 业务日志上下文
 */
public class BizLogContextHolder {

    // 这是存在问题的
    private static final ThreadLocal<Deque<Map<String, Object>>> VARIABLE_MAP_DEQUE = new TransmittableThreadLocal<>();

    private static final String COUNT_KEY = "count";

    public static Map<String, Object> getVariables() {
        return VARIABLE_MAP_DEQUE.get().peek();
    }

    @Nullable
    public static Object getVariable(String key) {
        Map<String, Object> map = getVariables();
        return Objects.isNull(map) ? null : map.get(key);
    }

    public static void setVariable(String key, Object value) {
        Deque<Map<String, Object>> deque = VARIABLE_MAP_DEQUE.get();
        if (Objects.isNull(deque)) {
            deque = new ArrayDeque<>();
            VARIABLE_MAP_DEQUE.set(deque);
        }
        VARIABLE_MAP_DEQUE.get().element().put(key, value);
    }

    public static void setCountVariable(int count) {
        setVariable(COUNT_KEY, count);
    }

    public static int getCountVariable() {
        Object count = getVariable(COUNT_KEY);
        return Objects.isNull(count) ? 1 : (int) count;
    }

    public static void clear() {
        if (Objects.nonNull(VARIABLE_MAP_DEQUE.get())) {
            VARIABLE_MAP_DEQUE.get().pop();
        }
    }

    /**
     * 日志使用方不需要使用到这个方法
     */
    public static void empty() {
        Deque<Map<String, Object>> deque = VARIABLE_MAP_DEQUE.get();
        if (Objects.isNull(deque)) {
            Deque<Map<String, Object>> s = new ArrayDeque<>();
            VARIABLE_MAP_DEQUE.set(s);
        }
        VARIABLE_MAP_DEQUE.get().push(new HashMap<>());
    }
}
