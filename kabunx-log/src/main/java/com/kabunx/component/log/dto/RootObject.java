package com.kabunx.component.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
public class RootObject {
    private final Method method;

    /**
     * 使用#{#root.args}时调用
     */
    private final Object[] args;

    /**
     * 使用#{#root.target}时调用
     */
    private final Object target;

    private final Class<?> targetClass;

    public RootObject(MethodExecute execute) {
        this.method = execute.getMethod();
        this.args = execute.getArgs();
        this.target = execute.getTarget();
        this.targetClass = execute.getTargetClass();
    }

    /**
     * 使用#{#root.methodName}时调用
     */
    public String getMethodName() {
        return this.method.getName();
    }
}
