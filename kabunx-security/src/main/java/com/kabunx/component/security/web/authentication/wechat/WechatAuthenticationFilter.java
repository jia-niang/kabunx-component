package com.kabunx.component.security.web.authentication.wechat;

import com.kabunx.component.security.dto.WechatRequest;
import com.kabunx.component.security.util.ServletRequestUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 微信登录过滤器
 */
public class WechatAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";
    public static final String SPRING_SECURITY_FORM_IV_KEY = "iv";
    public static final String SPRING_SECURITY_FORM_DATA_KEY = "data";

    protected WechatAuthenticationFilter() {
        super(new AntPathRequestMatcher("/wechat-login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        WechatRequest wechatRequest = ServletRequestUtils.getBodyObject(request, WechatRequest.class);
        WechatAuthenticationToken authRequest = new WechatAuthenticationToken(
                wechatRequest.getCode(), wechatRequest.getCode(), wechatRequest.getData()
        );
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(
            HttpServletRequest request,
            WechatAuthenticationToken authRequest
    ) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
