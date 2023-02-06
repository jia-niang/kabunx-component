package com.kabunx.component.security.web.authentication;

import com.kabunx.component.common.constant.SecurityConstants;
import com.kabunx.component.common.context.AuthContext;
import com.kabunx.component.common.dto.APIResponse;
import com.kabunx.component.common.util.JsonUtils;
import com.kabunx.component.jwt.JwtGenerator;
import com.kabunx.component.security.userdetails.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义认证成功的处理器
 * 将用户信息加入
 */
@Slf4j
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtGenerator jwtGenerator;

    public JwtAuthenticationSuccessHandler(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.warn("响应已经提交");
            return;
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Member member = (Member) authentication.getPrincipal();
        AuthContext authContext = getAuthContext(member, authentication.getAuthorities());
        String token = jwtGenerator.generateByHMAC(authContext);
        if (StringUtils.isEmpty(token)) {
            response.getWriter().print(
                    JsonUtils.object2Json(APIResponse.failure("JWT生成错误"))
            );
        } else {
            response.getWriter().print(
                    JsonUtils.object2Json(APIResponse.success(
                            SecurityConstants.AUTHENTICATION_PREFIX + token)
                    )
            );
        }
    }

    private AuthContext getAuthContext(Member member, Collection<? extends GrantedAuthority> authorities) {
        AuthContext authContext = new AuthContext();
        authContext.setId(member.getId());
        authContext.setType(member.getType());
        authContext.setUsername(member.getUsername());
        authContext.setAuthorities(getRoles(authorities));
        return authContext;
    }

    private Set<String> getRoles(Collection<? extends GrantedAuthority> authorities) {
        Set<String> roles = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
        return roles;
    }
}
