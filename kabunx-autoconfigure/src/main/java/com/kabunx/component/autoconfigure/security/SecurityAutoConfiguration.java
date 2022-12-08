package com.kabunx.component.autoconfigure.security;

import com.kabunx.component.jwt.JwtGenerator;
import com.kabunx.component.security.filter.JwtAuthenticationFilter;
import com.kabunx.component.security.web.authentication.JwtAuthenticationSuccessHandler;
import com.kabunx.component.security.web.authentication.RestAuthenticationFailureHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnClass({
        JwtGenerator.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationSuccessHandler.class,
        RestAuthenticationFailureHandler.class
})
public class SecurityAutoConfiguration {

    @Bean
    JwtGenerator jwtGenerator(SecurityProperties securityProperties) {
        return new JwtGenerator(securityProperties.getJwt());
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtGenerator jwtGenerator) {
        return new JwtAuthenticationFilter(jwtGenerator);
    }

    @Bean
    public JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler(JwtGenerator jwtGenerator) {
        return new JwtAuthenticationSuccessHandler(jwtGenerator);
    }

    @Bean
    public RestAuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }
}
