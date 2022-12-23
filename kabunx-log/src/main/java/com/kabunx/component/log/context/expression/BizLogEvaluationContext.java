package com.kabunx.component.log.context.expression;

import com.kabunx.component.log.annotation.BizLog;
import com.kabunx.component.log.context.BizLogContext;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 日志SpEl解析时的上下文信息
 * 凡是在${@link BizLogEvaluationContext#setVariable(String, Object)}中加入的变量，
 * 都可在${@link BizLog}注解的SpEL表达式中使用
 */
public class BizLogEvaluationContext extends MethodBasedEvaluationContext {

    private static final String RESULT = "result";

    private static final String ERROR_MSG = "errorMsg";

    public BizLogEvaluationContext(Object rootObject, Method method, Object[] arguments,
                                   ParameterNameDiscoverer parameterNameDiscoverer) {
        // 把方法的参数都放到 SpEL 解析的 RootObject 中
        super(rootObject, method, arguments, parameterNameDiscoverer);
        setBizLogContextVariables();
    }

    public void setBizLogContextVariables() {
        // 把 BizLogContext 中的变量都放到 RootObject 中
        Map<String, Object> variables = BizLogContext.getVariables();
        if (!CollectionUtils.isEmpty(variables)) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                setVariable(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setResultVariable(Object result) {
        setVariable(RESULT, result);
    }

    public void setErrorMsgVariable(String errorMsg) {
        setVariable(ERROR_MSG, errorMsg);
    }
}
