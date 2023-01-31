package com.kabunx.component.security.webflux.authentication;

import com.kabunx.component.common.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 将JWT转化为Authentication
 */
@Slf4j
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(SecurityConstants.AUTHENTICATION_PREFIX)) {
            return Mono.empty();
        }
        String token = authorization.replace(SecurityConstants.AUTHENTICATION_PREFIX, "");
        return Mono.just(new JwtAuthenticationToken("jwt", token));
    }
}
