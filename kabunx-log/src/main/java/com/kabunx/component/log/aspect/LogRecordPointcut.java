package com.kabunx.component.log.aspect;

import com.kabunx.component.log.LogRecordOperationSource;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

public class LogRecordPointcut extends StaticMethodMatcherPointcut implements Serializable {
    private LogRecordOperationSource logRecordOperationSource;

    /**
     * 解析 这个 method 上有没有 @LogRecord 注解，有的话会解析出来注解上的各个参数
     *
     * @param method      the candidate method
     * @param targetClass the target class
     * @return 是否存在注解
     */
    @Override
    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
        return !CollectionUtils.isEmpty(logRecordOperationSource.computeLogRecordOperations(method, targetClass));
    }


    public void setLogRecordOperationSource(LogRecordOperationSource logRecordOperationSource) {
        this.logRecordOperationSource = logRecordOperationSource;
    }
}
