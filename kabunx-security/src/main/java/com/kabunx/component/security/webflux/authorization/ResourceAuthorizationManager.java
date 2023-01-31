package com.kabunx.component.security.webflux.authorization;

import com.kabunx.component.common.constant.RequestConstants;
import com.kabunx.component.security.service.ResourceService;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 资源鉴权
 */
public class ResourceAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private final ResourceService resourceService;

    public ResourceAuthorizationManager(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();
        PathMatcher pathMatcher = new AntPathMatcher();
        // 1. 对应跨域的预检请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        // 2. token为空拒绝访问
        String token = request.getHeaders().getFirst(RequestConstants.HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(token)) {
            return Mono.just(new AuthorizationDecision(false));
        }
        // 3.缓存取资源权限角色关系列表
        Map<String, List<String>> resourceRoles = resourceService.get();
        // 4.请求路径匹配到的资源需要的角色权限集合authorities
        List<String> authorities = new ArrayList<>();
        if (Objects.nonNull(resourceRoles)) {
            for (String pattern : resourceRoles.keySet()) {
                if (pathMatcher.match(pattern, path)) {
                    authorities.addAll(resourceRoles.get(pattern));
                }
            }
        }
        // 5. roleId是请求用户的角色(格式:ROLE_{roleId})，authorities是请求资源所需要角色的集合
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
