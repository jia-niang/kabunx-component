package com.kabunx.component.autoconfigure.security;

import com.kabunx.component.jwt.JwtGenerator;
import com.kabunx.component.security.web.authentication.JwtAuthenticationSuccessHandler;
import com.kabunx.component.security.web.authentication.ServletAuthenticationFailureHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(JwtAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({JwtAuthenticationSuccessHandler.class, ServletAuthenticationFailureHandler.class})
public class WebSecurityAutoConfiguration {

    @Bean
    public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler(JwtGenerator jwtGenerator) {
        return new JwtAuthenticationSuccessHandler(jwtGenerator);
    }

    @Bean
    public ServletAuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new ServletAuthenticationFailureHandler();
    }
}
