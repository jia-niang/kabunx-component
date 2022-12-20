package com.kabunx.component.security.web.filter;

import com.kabunx.component.common.constant.SecurityConstants;
import com.kabunx.component.jwt.JwtGenerator;
import com.kabunx.component.jwt.JwtPayload;
import com.kabunx.component.security.userdetails.Member;
import com.kabunx.component.security.web.RestAuthenticationEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtGenerator jwtGenerator;

    /**
     * 认证如果失败由该端点进行响应
     */
    private final AuthenticationEntryPoint authenticationEntryPoint = new RestAuthenticationEntryPoint();

    public JwtAuthenticationFilter(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 如果已经通过认证
        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            chain.doFilter(request, response);
            return;
        }
        // 获取 header 解析出 jwt 并进行认证
        // 无token 直接进入下一个过滤器  因为  SecurityContext 的缘故 如果无权限并不会放行
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(SecurityConstants.AUTHENTICATION_PREFIX)) {
            String token = header.replace(SecurityConstants.AUTHENTICATION_PREFIX, "");
            if (StringUtils.hasText(token)) {
                try {
                    authenticationTokenHandle(token, request);
                } catch (AuthenticationException e) {
                    authenticationEntryPoint.commence(request, response, e);
                }
            } else {
                // 带安全头 没有带token
                authenticationEntryPoint.commence(request, response, new AuthenticationCredentialsNotFoundException("token is not found"));
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 具体的认证方法  匿名访问不要携带token
     *
     * @param token   jwt token
     * @param request request
     */
    private void authenticationTokenHandle(String token, HttpServletRequest request) throws AuthenticationException {
        // 有效token才会被解析出来 包含 用户简单信息
        JwtPayload payload = jwtGenerator.verifyByHMAC(token);
        if (Objects.nonNull(payload)) {
            // 通过用户获取角色或权限
            String[] roles = payload.getAuthorities().toArray(new String[0]);
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles);

            Member user = new Member(Long.valueOf(payload.getAud()), payload.getType(), payload.getUsername(), "PASSWORD", authorities);
            // 构建用户认证token
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 放入安全上下文中
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else {
            log.error("token : {}  is  invalid", token);
            throw new BadCredentialsException("token is invalid");
        }
    }
}