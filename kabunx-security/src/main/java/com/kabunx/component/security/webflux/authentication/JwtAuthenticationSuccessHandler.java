package com.kabunx.component.security.webflux.authentication;

import com.kabunx.component.common.constant.RequestConstants;
import com.kabunx.component.security.userdetails.Member;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange filterExchange, Authentication authentication) {
        ServerWebExchange exchange = filterExchange.getExchange();
        if (Objects.nonNull(authentication.getDetails())) {
            Member member = (Member) authentication.getDetails();
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(headers -> {
                        headers.set(RequestConstants.HEADER_AUTH_TYPE, member.getType());
                        headers.set(RequestConstants.HEADER_AUTH_ID, String.valueOf(member.getId()));
                    })
                    .build();
            exchange = exchange.mutate().request(request).build();
        }
        return filterExchange.getChain().filter(exchange);
    }
}
