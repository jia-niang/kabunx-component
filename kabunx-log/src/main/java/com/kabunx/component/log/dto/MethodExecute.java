package com.kabunx.component.log.dto;

import lombok.Data;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 函数执行信息
 */
@Data
public class MethodExecute {
    private final Method method;
    private final Object[] args;
    private final Object target;
    private final Class<?> targetClass;

    /**
     * 上下文变量
     */
    private Map<String, Object> contextVariables;

    // 执行结果
    private boolean success;
    private Object result;
    private Throwable throwable;
    private String errorMsg;

    public MethodExecute(MethodInvocation invocation) {
        this.method = invocation.getMethod();
        this.args = invocation.getArguments();
        this.target = invocation.getThis();
        this.targetClass = AopProxyUtils.ultimateTargetClass(target);
    }

    public MethodExecute(ProceedingJoinPoint point) {
        this.method = ((MethodSignature) point.getSignature()).getMethod();
        this.args = point.getArgs();
        this.target = point.getTarget();
        this.targetClass = point.getTarget().getClass();
    }
}
