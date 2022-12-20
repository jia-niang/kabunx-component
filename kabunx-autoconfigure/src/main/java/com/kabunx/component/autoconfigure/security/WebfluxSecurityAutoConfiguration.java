package com.kabunx.component.autoconfigure.security;

import com.kabunx.component.jwt.JwtGenerator;
import com.kabunx.component.security.service.ResourceService;
import com.kabunx.component.security.webflux.JwtAuthenticationEntryPoint;
import com.kabunx.component.security.webflux.authentication.JwtAuthenticationConverter;
import com.kabunx.component.security.webflux.authentication.JwtAuthenticationWebFilter;
import com.kabunx.component.security.webflux.authentication.JwtReactiveAuthenticationManager;
import com.kabunx.component.security.webflux.authorization.ResourceAuthorizationManager;
import com.kabunx.component.security.webflux.authorization.ResourceAccessDeniedHandler;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(JwtAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebfluxSecurityAutoConfiguration {

    /**
     * @param jwtGenerator JWT生成器
     * @return JWT认证管理器
     */
    @Bean
    @ConditionalOnClass(JwtReactiveAuthenticationManager.class)
    @ConditionalOnBean(JwtGenerator.class)
    JwtReactiveAuthenticationManager reactiveAuthenticationManager(JwtGenerator jwtGenerator) {
        return new JwtReactiveAuthenticationManager(jwtGenerator);
    }

    /**
     * @param jwtReactiveAuthenticationManager JWT认证管理器
     * @return JWT过滤器
     */
    @Bean
    @ConditionalOnClass(JwtAuthenticationWebFilter.class)
    JwtAuthenticationWebFilter jwtAuthenticationWebFilter(JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager) {
        JwtAuthenticationWebFilter filter = new JwtAuthenticationWebFilter(jwtReactiveAuthenticationManager);
        filter.setServerAuthenticationConverter(new JwtAuthenticationConverter());
        return filter;
    }

    /**
     * @return jwt认证失败端口
     */
    @Bean
    @ConditionalOnClass(JwtAuthenticationEntryPoint.class)
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    /**
     * @param resourceService 资源服务
     * @return 资源认证管理器
     */
    @Bean
    @ConditionalOnClass(ResourceAuthorizationManager.class)
    @ConditionalOnBean(ResourceService.class)
    ResourceAuthorizationManager resourceAuthorizationManager(ResourceService resourceService) {
        return new ResourceAuthorizationManager(resourceService);
    }

    /**
     * 未授权响应处理器
     *
     * @return handler
     */
    @Bean
    @ConditionalOnClass(ResourceAccessDeniedHandler.class)
    ResourceAccessDeniedHandler resourceAccessDeniedHandler() {
        return new ResourceAccessDeniedHandler();
    }
}
