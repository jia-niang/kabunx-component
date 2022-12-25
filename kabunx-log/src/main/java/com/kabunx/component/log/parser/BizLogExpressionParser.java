package com.kabunx.component.log.parser;

import com.kabunx.component.log.context.expression.BizLogExpressionEvaluator;
import com.kabunx.component.log.dto.ExpressionArgs;
import com.kabunx.component.log.dto.MethodExecute;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包装参数发给{@link BizLogExpressionEvaluator}解析的工具类
 */
public class BizLogExpressionParser implements BeanFactoryAware {

    private final BizLogExpressionEvaluator expressionEvaluator = new BizLogExpressionEvaluator();


    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * @param execute 方法执行信息
     * @return 解析参数
     */
    public ExpressionArgs getExpressionArgs(MethodExecute execute) {
        EvaluationContext evaluationContext = expressionEvaluator.buildEvaluationContext(execute, beanFactory);
        AnnotatedElementKey methodKey = new AnnotatedElementKey(execute.getMethod(), execute.getTargetClass());
        return new ExpressionArgs(evaluationContext, methodKey);
    }

    public String doParse(String expression, ExpressionArgs args) {
        return expressionEvaluator.parse(expression, args, String.class);
    }

    public Map<String, String> doParse(List<String> expressions, ExpressionArgs args) {
        Map<String, String> expressionMap = new HashMap<>();
        expressions.stream()
                .filter(expression -> !StringUtils.isEmpty(expression))
                .forEach(expression -> {
                    String result = doParse(expression, args);
                    expressionMap.put(expression, result);
                });
        return expressionMap;
    }
}
