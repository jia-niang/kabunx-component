package com.kabunx.component.security.webflux.authentication;

import com.kabunx.component.jwt.JwtGenerator;
import com.kabunx.component.jwt.JwtPayload;
import com.kabunx.component.security.userdetails.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtGenerator jwtGenerator;

    public JwtReactiveAuthenticationManager(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return Mono.just(authentication);
        }
        JwtAuthenticationToken jat = (JwtAuthenticationToken) authentication;
        log.info("[Security] JWT is {}", jat.getCredentials());
        // 有效token才会被解析出来 包含 用户简单信息
        JwtPayload payload = jwtGenerator.verifyByHMAC((String) jat.getCredentials());
        if (Objects.nonNull(payload)) {
            // 通过用户获取角色或权限
            String[] roles = {};
            if (Objects.nonNull(payload.getAuthorities())) {
                roles = payload.getAuthorities().toArray(new String[0]);
            }
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles);
            Member user = new Member(Long.valueOf(payload.getAud()), payload.getType(), payload.getUsername(), "PASSWORD", authorities);
            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(user.getUsername(), jat.getCredentials(), authorities);
            authenticationToken.setDetails(user);
            return Mono.just(authenticationToken);
        } else {
            log.error("[Security] JWT : {} is invalid.", jat.getCredentials());
            throw new BadCredentialsException("JWT is invalid.");
        }
    }
}
