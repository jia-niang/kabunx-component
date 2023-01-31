package com.kabunx.component.log.context.expression;

import com.kabunx.component.log.dto.ExpressionArgs;
import com.kabunx.component.log.dto.RootObject;
import com.kabunx.component.log.dto.MethodExecute;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于解析日志的SpEL表达式
 */
public class BizLogExpressionEvaluator {
    private static final String EXPRESSION_PREFIX = "{";

    private static final String EXPRESSION_SUFFIX = "}";

    private final SpelExpressionParser parser;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final TemplateParserContext templateParserContext = new TemplateParserContext(EXPRESSION_PREFIX, EXPRESSION_SUFFIX);

    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(64);
    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    public BizLogExpressionEvaluator() {
        this(new SpelExpressionParser());
    }

    public BizLogExpressionEvaluator(SpelExpressionParser parser) {
        this.parser = parser;
    }

    /**
     * 解析指定表达式。
     */
    public Object parse(String expression, AnnotatedElementKey methodKey, EvaluationContext context) {
        return getExpression(this.expressionCache, methodKey, expression).getValue(context, Object.class);
    }

    public <T> T parse(String expression, AnnotatedElementKey methodKey, EvaluationContext context, Class<T> tClass) {
        return getExpression(this.expressionCache, methodKey, expression).getValue(context, tClass);
    }

    /**
     * 解析表达式并返回解析结果
     */
    public <T> T parse(String expression, ExpressionArgs args, Class<T> tClass) {
        return getExpression(this.expressionCache, args.getMethodKey(), expression).getValue(args.getContext(), tClass);
    }

    /**
     * 创建表达式计算上下文{@link EvaluationContext}.
     *
     * @param execute     方法执行器
     * @param beanFactory bean factory
     * @return EvaluationContext
     */
    public EvaluationContext buildEvaluationContext(MethodExecute execute, @Nullable BeanFactory beanFactory) {
        RootObject rootObject = new RootObject(execute);
        Method targetMethod = getTargetMethod(execute.getMethod(), execute.getTargetClass());
        BizLogEvaluationContext evaluationContext = new BizLogEvaluationContext(rootObject, targetMethod, execute.getArgs(), getParameterNameDiscoverer());
        evaluationContext.setBizLogContextVariables(execute.getContextVariables());
        evaluationContext.setResultVariable(execute.getResult());
        evaluationContext.setErrorMsgVariable(execute.getErrorMsg());
        if (Objects.nonNull(beanFactory)) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    protected SpelExpressionParser getParser() {
        return this.parser;
    }

    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    protected Expression getExpression(Map<ExpressionKey, Expression> cache,
                                       AnnotatedElementKey elementKey, String expression) {
        ExpressionKey expressionKey = createKey(elementKey, expression);
        Expression expr = cache.get(expressionKey);
        if (expr == null) {
            expr = getParser().parseExpression(expression, templateParserContext);
            cache.put(expressionKey, expr);
        }
        return expr;
    }

    private ExpressionKey createKey(AnnotatedElementKey elementKey, String expression) {
        return new ExpressionKey(elementKey, expression);
    }

    /**
     * An expression key.
     */
    protected static class ExpressionKey implements Comparable<ExpressionKey> {

        private final AnnotatedElementKey element;

        private final String expression;

        protected ExpressionKey(AnnotatedElementKey element, String expression) {
            Assert.notNull(element, "AnnotatedElementKey must not be null");
            Assert.notNull(expression, "Expression must not be null");
            this.element = element;
            this.expression = expression;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ExpressionKey)) {
                return false;
            }
            ExpressionKey otherKey = (ExpressionKey) other;
            return (this.element.equals(otherKey.element) &&
                    ObjectUtils.nullSafeEquals(this.expression, otherKey.expression));
        }

        @Override
        public int hashCode() {
            return this.element.hashCode() * 29 + this.expression.hashCode();
        }

        @Override
        public String toString() {
            return this.element + " with expression \"" + this.expression + "\"";
        }

        @Override
        public int compareTo(ExpressionKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0) {
                result = this.expression.compareTo(other.expression);
            }
            return result;
        }
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
