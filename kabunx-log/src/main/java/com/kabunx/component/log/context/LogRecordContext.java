package com.kabunx.component.log.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class LogRecordContext {

    private static final TransmittableThreadLocal<Stack<Map<String, Object>>> VARIABLE_MAP_STACK = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<Map<String, Object>> GLOBAL_VARIABLE_MAP = new TransmittableThreadLocal<>();

    public static Map<String, Object> getVariables() {
        return VARIABLE_MAP_STACK.get().peek();
    }

    public static Object getVariable(String key) {
        Map<String, Object> variableMap = getVariables();
        return Objects.isNull(variableMap) ? null : variableMap.get(key);
    }

    public static void setVariable(String key, Object value) {
        if (Objects.isNull(VARIABLE_MAP_STACK.get())) {
            Stack<Map<String, Object>> stack = new Stack<>();
            VARIABLE_MAP_STACK.set(stack);
        }
        Stack<Map<String, Object>> mapStack = VARIABLE_MAP_STACK.get();
        if (mapStack.isEmpty()) {
            VARIABLE_MAP_STACK.get().push(new HashMap<>());
        }
        VARIABLE_MAP_STACK.get().peek().put(key, value);
    }

    public static void clear() {
        if (Objects.nonNull(VARIABLE_MAP_STACK.get())) {
            VARIABLE_MAP_STACK.get().pop();
        }
        if (Objects.isNull(VARIABLE_MAP_STACK.get().peek())) {
            GLOBAL_VARIABLE_MAP.remove();
        }
    }

    /**
     * 日志使用方不需要使用到这个方法
     * 每进入一个方法初始化一个 span 放入到 stack中，方法执行完后 pop 掉这个span
     */
    public static void empty() {
        Stack<Map<String, Object>> mapStack = VARIABLE_MAP_STACK.get();
        if (Objects.isNull(mapStack)) {
            Stack<Map<String, Object>> stack = new Stack<>();
            VARIABLE_MAP_STACK.set(stack);
        }
        VARIABLE_MAP_STACK.get().push(new HashMap<>());
        if (GLOBAL_VARIABLE_MAP.get() == null) {
            GLOBAL_VARIABLE_MAP.set(new HashMap<>());
        }
    }
}
