package com.kabunx.component.log.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.*;

/**
 * 业务日志上下文
 */
public class BizLogContextHolder {

    // 这是存在问题的
    private static final ThreadLocal<Map<String, Object>> VARIABLE_MAP = new TransmittableThreadLocal<>();

    private static final String COUNT_KEY = "count";

    public static Map<String, Object> getVariables() {
        return VARIABLE_MAP.get();
    }

    public static Object getVariable(String key) {
        Map<String, Object> variableMap = getVariables();
        return Objects.isNull(variableMap) ? null : variableMap.get(key);
    }

    public static void setVariable(String key, Object value) {
        Map<String, Object> map = VARIABLE_MAP.get();
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }
        map.put(key, value);
        VARIABLE_MAP.set(map);
    }

    public static void setCountVariable(int count) {
        setVariable(COUNT_KEY, count);
    }

    public static int getCountVariable() {
        Object count = getVariable(COUNT_KEY);
        return Objects.isNull(count) ? 1 : (int) count;
    }

    public static void clear() {
        VARIABLE_MAP.remove();
    }

    /**
     * 日志使用方不需要使用到这个方法
     */
    public static void empty() {
        VARIABLE_MAP.set(new HashMap<>());
    }
}
