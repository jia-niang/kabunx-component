package com.kabunx.component.log.parser;

import com.kabunx.component.log.context.expression.BizLogExpressionEvaluator;
import com.kabunx.component.log.dto.ExpressionArgs;
import com.kabunx.component.log.dto.MethodExecute;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * 包装参数发给{@link BizLogExpressionEvaluator}解析的工具类
 */
public class BizLogAspectParser implements BeanFactoryAware {
    private final BizLogExpressionEvaluator expressionEvaluator = new BizLogExpressionEvaluator();
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public <T> T parse(ProceedingJoinPoint point, Supplier<String> success,
                       @Nullable Object result, @Nullable String errorMsg, Class<T> tClass) {
        return buildAspectParser(expressionEvaluator::parse, success, tClass).parse(point, result, errorMsg);
    }

    private <T> AspectParser<T> buildAspectParser(Parser parser, Supplier<String> getExpression, Class<T> tClass) {
        return (point, proceed, errorMsg) -> {
            ExpressionArgs expressionArgs = getExpressionArgs(point, proceed, errorMsg);
            return parser.parse(getExpression.get(), expressionArgs, tClass);
        };
    }

    private ExpressionArgs getExpressionArgs(ProceedingJoinPoint point, @Nullable Object result, @Nullable String errorMsg) {
        MethodExecute execute = new MethodExecute(point);
        execute.setSuccess(true);
        execute.setResult(result);
        execute.setErrorMsg(errorMsg);
        EvaluationContext evaluationContext = expressionEvaluator.buildEvaluationContext(execute, beanFactory);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(execute.getMethod(), execute.getTargetClass());
        return new ExpressionArgs(evaluationContext, methodKey);
    }

    @FunctionalInterface
    private interface AspectParser<T> {
        T parse(ProceedingJoinPoint point, @Nullable Object result, @Nullable String errorMsg);
    }

    @FunctionalInterface
    private interface Parser {
        <T> T parse(String expression, ExpressionArgs args, Class<T> tClass);
    }
}
