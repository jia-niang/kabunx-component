package com.kabunx.component.autoconfigure.web;

import com.kabunx.component.web.servlet.AuthHandlerInterceptor;
import com.kabunx.component.web.servlet.TraceHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class InterceptorAutoConfiguration {
    @Bean
    @ConditionalOnClass(TraceHandlerInterceptor.class)
    public TraceHandlerInterceptor traceHandlerInterceptor() {
        return new TraceHandlerInterceptor();
    }

    @Bean
    @ConditionalOnClass(AuthHandlerInterceptor.class)
    public AuthHandlerInterceptor authHandlerInterceptor() {
        return new AuthHandlerInterceptor();
    }
}
