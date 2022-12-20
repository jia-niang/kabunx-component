package com.kabunx.component.security.web.authentication.captcha;

import com.kabunx.component.security.dto.CaptchaRequest;
import com.kabunx.component.security.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
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
 * 验证码登录过滤器
 */
@Slf4j
public class CaptchaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public CaptchaAuthenticationFilter() {
        super(new AntPathRequestMatcher("/captcha-login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        CaptchaRequest captchaRequest = RequestUtils.getBodyObject(request, CaptchaRequest.class);
        CaptchaAuthenticationToken authRequest = new CaptchaAuthenticationToken(
                captchaRequest.getPhone(), captchaRequest.getCaptcha(), captchaRequest.getType()
        );
        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, CaptchaAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
}
