package com.kabunx.component.security.web;

import com.kabunx.component.common.dto.APIResponse;
import com.kabunx.component.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 认证失败入口点
 * 当用户访问需要认证接口时，认证失败的处理逻辑
 */
@Slf4j
public class ServletAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.warn("响应已经提交");
            return;
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(
                JsonUtils.object2Json(APIResponse.failure("认证失败，请先登录！"))
        );
    }
}
