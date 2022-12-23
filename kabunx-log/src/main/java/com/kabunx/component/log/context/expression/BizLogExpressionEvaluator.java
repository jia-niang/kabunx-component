package com.kabunx.component.log.context.expression;

import com.kabunx.component.log.dto.ExpressionArgs;
import com.kabunx.component.log.dto.RootObject;
import com.kabunx.component.log.dto.MethodExecute;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于解析日志的SpEL表达式
 */
public class BizLogExpressionEvaluator extends CachedExpressionEvaluator {
    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);
    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    /**
     * 解析指定表达式。
     */
    public Object parse(String expression, AnnotatedElementKey methodKey, EvaluationContext context) {
        return getExpression(this.expressionCache, methodKey, expression).getValue(context, Object.class);
    }

    public <T> T parse(String expression, AnnotatedElementKey methodKey, EvaluationContext context, Class<T> tClass) {
        return getExpression(this.expressionCache, methodKey, expression).getValue(context, tClass);
    }

    public <T> T parse(String expression, ExpressionArgs args, Class<T> tClass) {
        return getExpression(this.expressionCache, args.getMethodKey(), expression).getValue(args.getContext(), tClass);
    }

    /**
     * Build an {@link EvaluationContext}.
     *
     * @param execute     方法执行器
     * @param beanFactory bean factory
     * @return EvaluationContext
     */
    public EvaluationContext buildEvaluationContext(MethodExecute execute, @Nullable BeanFactory beanFactory) {
        RootObject rootObject = new RootObject(execute);
        Method targetMethod = getTargetMethod(execute.getMethod(), execute.getTargetClass());
        BizLogEvaluationContext evaluationContext = new BizLogEvaluationContext(rootObject, targetMethod, execute.getArgs(), getParameterNameDiscoverer());
        if (Objects.nonNull(beanFactory)) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    /**
     * 获取真正的具体的方法并缓存
     *
     * @param method      来自接口或者父类的方法签名
     * @param targetClass 目标class
     * @return 目标class实现的具体方法
     */
    private Method getTargetMethod(Method method, Class<?> targetClass) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        return targetMethodCache.computeIfAbsent(methodKey, k -> AopUtils.getMostSpecificMethod(method, targetClass));
    }
}
