package com.kabunx.component.security.webflux;

import com.kabunx.component.security.util.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * webflux认证失败
 */
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return ResponseUtils.failure(exchange, "认证失败！");
    }
}
