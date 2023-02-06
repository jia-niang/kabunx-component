package com.kabunx.component.web.servlet;

import com.kabunx.component.common.constant.RequestConstants;
import com.kabunx.component.common.context.AuthContext;
import com.kabunx.component.common.context.AuthContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 提取头信息中的用户信息
 */
@Slf4j
public class AuthHandlerInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String userType = request.getHeader(RequestConstants.HEADER_AUTH_TYPE);
        String userId = request.getHeader(RequestConstants.HEADER_AUTH_ID);
        if (StringUtils.hasText(userType) && StringUtils.hasText(userId)) {
            AuthContext authContext = new AuthContext();
            authContext.setType(userType);
            authContext.setId(Long.valueOf(userId));
            AuthContextHolder.setCurrentAuth(authContext);
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) throws Exception {
        AuthContextHolder.removeCurrentAuth();
        super.afterCompletion(request, response, handler, ex);
    }
}
