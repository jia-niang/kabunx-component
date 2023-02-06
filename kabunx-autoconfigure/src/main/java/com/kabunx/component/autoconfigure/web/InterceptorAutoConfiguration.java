package com.kabunx.component.autoconfigure.web;

import com.kabunx.component.common.constant.OrderedConstants;
import com.kabunx.component.web.servlet.AuthHandlerInterceptor;
import com.kabunx.component.web.servlet.LogHandlerInterceptor;
import com.kabunx.component.web.servlet.TraceHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class InterceptorAutoConfiguration {
    @Bean
    @Order(OrderedConstants.TRACE_HANDLER)
    @ConditionalOnClass(TraceHandlerInterceptor.class)
    public TraceHandlerInterceptor traceHandlerInterceptor() {
        return new TraceHandlerInterceptor();
    }

    @Bean
    @Order(OrderedConstants.AUTH_HANDLER)
    @ConditionalOnClass(AuthHandlerInterceptor.class)
    public AuthHandlerInterceptor authHandlerInterceptor() {
        return new AuthHandlerInterceptor();
    }

    @Bean
    @Order(OrderedConstants.LOG_HANDLER)
    @ConditionalOnClass(LogHandlerInterceptor.class)
    public LogHandlerInterceptor logHandlerInterceptor() {
        return new LogHandlerInterceptor();
    }
}
