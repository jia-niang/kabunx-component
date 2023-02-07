package com.kabunx.component.web.annotation;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * API 版本号处理
 */
public class APIVersionHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected boolean isHandler(@NonNull Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
    }

    @Override
    protected void registerHandlerMethod(@NonNull Object handler, @NonNull Method method, @NonNull RequestMappingInfo mapping) {
        Class<?> controllerClass = method.getDeclaringClass();
        // 类上的 APIVersion 注解
        APIVersion apiVersion = AnnotationUtils.findAnnotation(controllerClass, APIVersion.class);
        // 方法上的 APIVersion 注解
        APIVersion methodAnnotation = AnnotationUtils.findAnnotation(method, APIVersion.class);
        // 以方法上的注解优先
        if (Objects.nonNull(methodAnnotation)) {
            apiVersion = methodAnnotation;
        }
        String[] urlPatterns = apiVersion == null ? new String[0] : apiVersion.value();
        PatternsRequestCondition apiPattern = new PatternsRequestCondition(urlPatterns);
        PatternsRequestCondition oldPattern = mapping.getPatternsCondition();
        PatternsRequestCondition updatedFinalPattern = apiPattern.combine(oldPattern);

        // 重新构建 RequestMappingInfo
        mapping = new RequestMappingInfo(mapping.getName(), updatedFinalPattern,
                mapping.getMethodsCondition(), mapping.getParamsCondition(),
                mapping.getHeadersCondition(), mapping.getConsumesCondition(),
                mapping.getProducesCondition(), mapping.getCustomCondition());
        super.registerHandlerMethod(handler, method, mapping);
    }
}
