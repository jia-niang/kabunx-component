package com.kabunx.component.log.aop;

import com.kabunx.component.log.parser.OperationLogParser;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

public class OperationLogPointcut extends StaticMethodMatcherPointcut implements Serializable {
    private OperationLogParser operationLogParser;

    /**
     * 解析 这个 method 上有没有 @LogRecord 注解，有的话会解析出来注解上的各个参数
     *
     * @param method      the candidate method
     * @param targetClass the target class
     * @return 是否存在注解
     */
    @Override
    public boolean matches(@NonNull Method method, @NonNull Class<?> targetClass) {
        return !CollectionUtils.isEmpty(operationLogParser.buildLogRecordOperations(method, targetClass));
    }


    public void setLogRecordParser(OperationLogParser operationLogParser) {
        this.operationLogParser = operationLogParser;
    }
}
