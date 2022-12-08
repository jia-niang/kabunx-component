package com.kabunx.component.autoconfigure.web;

import com.kabunx.component.web.servlet.AuthHandlerInterceptor;
import com.kabunx.component.web.servlet.TraceHandlerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({
        TraceHandlerInterceptor.class,
        AuthHandlerInterceptor.class
})
public class InterceptorAutoConfiguration {
    @Bean
    public TraceHandlerInterceptor traceHandlerInterceptor() {
        return new TraceHandlerInterceptor();
    }

    @Bean
    public AuthHandlerInterceptor authHandlerInterceptor() {
        return new AuthHandlerInterceptor();
    }
}
