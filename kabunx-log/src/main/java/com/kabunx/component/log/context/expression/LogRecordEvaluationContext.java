package com.kabunx.component.log.context.expression;

import com.kabunx.component.log.context.LogRecordContext;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class LogRecordEvaluationContext extends MethodBasedEvaluationContext {
    public LogRecordEvaluationContext(Object rootObject, Method method, Object[] arguments,
                                      ParameterNameDiscoverer parameterNameDiscoverer, Object result, String errorMsg) {
        // 把方法的参数都放到 SpEL 解析的 RootObject 中
        super(rootObject, method, arguments, parameterNameDiscoverer);
        // 把 LogRecordContext 中的变量都放到 RootObject 中
        Map<String, Object> variables = LogRecordContext.getVariables();
        if (!CollectionUtils.isEmpty(variables)) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                setVariable(entry.getKey(), entry.getValue());
            }
        }
        // 把方法的返回值和 ErrorMsg 都放到 RootObject 中
        setVariable("_result", result);
        setVariable("_errorMsg", errorMsg);
    }
}
