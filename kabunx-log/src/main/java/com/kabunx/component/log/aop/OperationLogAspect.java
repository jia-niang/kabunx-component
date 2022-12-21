package com.kabunx.component.log.aop;

import com.kabunx.component.log.annotation.OperationLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class OperationLogAspect {

    // 创建切点
    @Pointcut("@annotation(com.kabunx.component.log.annotation.OperationLog)")
    public void operationLogMethod() {
    }

    // 在切点前后执行方法，通过 @annotation(logRecordAnnotation) 绑定注解到第二个参数
    // ProceedingJoinPoint 必须要放在第一个参数
    @Around("operationLogMethod() && @annotation(operationLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        joinPoint.getThis();
        joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        return null;
    }
}
