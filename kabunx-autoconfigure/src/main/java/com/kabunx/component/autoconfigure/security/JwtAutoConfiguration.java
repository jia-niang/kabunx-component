package com.kabunx.component.autoconfigure.security;

import com.kabunx.component.jwt.JwtConfig;
import com.kabunx.component.jwt.JwtGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnClass(JwtGenerator.class)
public class JwtAutoConfiguration {

    @Bean
    JwtGenerator jwtGenerator(SecurityProperties properties) {
        return new JwtGenerator(jwtConfig(properties.getJwt()));
    }

    private JwtConfig jwtConfig(SecurityProperties.Jwt jwt) {
        JwtConfig config = new JwtConfig();
        if (Objects.isNull(jwt)) {
            return config;
        }
        if (Objects.nonNull(jwt.getIss())) {
            config.setIss(jwt.getIss());
        }
        if (Objects.nonNull(jwt.getSub())) {
            config.setSub(jwt.getSub());
        }
        config.setAccessExpDays(jwt.getAccessExpDays());
        config.setRefreshExpDays(jwt.getRefreshExpDays());

        return config;
    }
}
