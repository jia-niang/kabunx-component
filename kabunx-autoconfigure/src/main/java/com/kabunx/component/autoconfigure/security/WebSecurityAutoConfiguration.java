package com.kabunx.component.autoconfigure.security;

import com.kabunx.component.jwt.JwtGenerator;
import com.kabunx.component.security.web.authentication.JwtAuthenticationSuccessHandler;
import com.kabunx.component.security.web.authentication.RestAuthenticationFailureHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(JwtAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSecurityAutoConfiguration {

    @Bean
    @ConditionalOnClass(JwtAuthenticationSuccessHandler.class)
    public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler(JwtGenerator jwtGenerator) {
        return new JwtAuthenticationSuccessHandler(jwtGenerator);
    }

    @Bean
    @ConditionalOnClass(RestAuthenticationFailureHandler.class)
    public RestAuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }
}
