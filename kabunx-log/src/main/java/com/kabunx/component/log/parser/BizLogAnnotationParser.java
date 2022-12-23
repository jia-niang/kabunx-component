package com.kabunx.component.log.parser;

import com.kabunx.component.log.annotation.BizLog;
import com.kabunx.component.log.dto.BizLogExpression;
import com.kabunx.component.log.dto.MethodExecute;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BizLogAnnotationParser {

    /**
     * 判断是否存在 @BizLog 注解
     */
    public boolean matches(Method method, Class<?> targetClass) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return false;
        }
        Collection<BizLog> bizLogAnnotations = getAllAnnotations(method, targetClass);
        return !bizLogAnnotations.isEmpty();
    }

    public Collection<BizLogExpression> getBizLogExpressions(Method method, Class<?> targetClass) {
        // 不允许使用非公共方法。
        if (!Modifier.isPublic(method.getModifiers())) {
            return Collections.emptyList();
        }
        Collection<BizLog> bizLogAnnotations = getAllAnnotations(method, targetClass);
        return buildBizLogExpressionsFromAnnotations(bizLogAnnotations);
    }

    public Collection<BizLogExpression> getBizLogExpressions(MethodExecute execute) {
        return getBizLogExpressions(execute.getMethod(), execute.getTargetClass());
    }

    /**
     * 找到具体实现类的方法
     *
     * @param method      方法
     * @param targetClass 目标class
     * @return Method
     */
    private Collection<BizLog> getAllAnnotations(Method method, Class<?> targetClass) {
        // 该方法可能位于接口上，但我们需要目标类（具体实现类）的属性。
        // 如果目标类为 null，则该方法将保持不变。
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // 如果我们正在处理带有通用参数的方法，请找到原始方法。
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        // 首先尝试的是目标类中的方法。
        // TODO 是否需要走接口中的方法
        return AnnotatedElementUtils.findAllMergedAnnotations(specificMethod, BizLog.class);
    }

    private Collection<BizLogExpression> buildBizLogExpressionsFromAnnotations(Collection<BizLog> bizLogAnnotations) {
        Collection<BizLogExpression> expressions = new ArrayList<>();
        if (!bizLogAnnotations.isEmpty()) {
            for (BizLog bizLogAnnotation : bizLogAnnotations) {
                expressions.add(buildBizLogExpressionFromAnnotation(bizLogAnnotation));
            }
        }
        return expressions;
    }

    private BizLogExpression buildBizLogExpressionFromAnnotation(BizLog bizLog) {
        return BizLogExpression.builder()
                .success(bizLog.success())
                .error(bizLog.error())
                .bizNo(bizLog.bizNo())
                .operator(bizLog.operator())
                .condition(bizLog.condition())
                .build();
    }
}
