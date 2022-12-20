package com.kabunx.component.security.webflux.authorization;

import com.kabunx.component.common.dto.RestResponse;
import com.kabunx.component.security.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 资源权限受限
 */
@Slf4j
public class ResourceAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        log.error("该资源权限受限", denied);
        return ResponseUtils.defer(exchange, RestResponse.failure("该资源权限受限！"));
    }
}