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
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return Mono.just(jwtAuthenticationToken)
                .map(jat -> {
                    log.info("[Security] jwt is {}", jat.getCredentials());
                    // 有效token才会被解析出来 包含 用户简单信息
                    JwtPayload payload = jwtGenerator.verifyByHMAC((String) jat.getCredentials());
                    if (Objects.nonNull(payload)) {
                        // 通过用户获取角色或权限
                        String[] roles = payload.getAuthorities().toArray(new String[0]);
                        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles);
                        Member user = new Member(Long.valueOf(payload.getAud()), payload.getType(), payload.getUsername(), "PASSWORD", authorities);
                        return new JwtAuthenticationToken(user.getUsername(), jat.getCredentials(), authorities);
                    } else {
                        throw new BadCredentialsException("token is invalid");
                    }
                });
    }
}
