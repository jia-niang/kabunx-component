package com.kabunx.component.log.aspect;

import com.kabunx.component.log.annotation.LogRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LogRecordAspect {

    // 创建切点
    @Pointcut("@annotation(com.kabunx.component.log.annotation.LogRecord)")
    public void logRecordMethod() {
    }

    // 在切点前后执行方法，通过 @annotation(logRecordAnnotation) 绑定注解到第二个参数
    // ProceedingJoinPoint 必须要放在第一个参数
    @Around("logRecordMethod() && @annotation(logRecord)")
    public Object BeforeMethodStart(ProceedingJoinPoint joinPoint, LogRecord logRecord) throws Throwable {
        return null;
    }
}
