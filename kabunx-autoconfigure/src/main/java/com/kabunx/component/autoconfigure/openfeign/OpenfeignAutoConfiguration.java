package com.kabunx.component.autoconfigure.openfeign;

import com.kabunx.component.openfeign.AuthRequestInterceptor;
import com.kabunx.component.openfeign.TraceIdRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OpenfeignAutoConfiguration {

    @Bean
    @ConditionalOnClass(AuthRequestInterceptor.class)
    AuthRequestInterceptor authRequestInterceptor() {
        return new AuthRequestInterceptor();
    }

    @Bean
    @ConditionalOnClass(TraceIdRequestInterceptor.class)
    TraceIdRequestInterceptor traceRequestInterceptor() {
        return new TraceIdRequestInterceptor();
    }
}
