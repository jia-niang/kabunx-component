package com.kabunx.component.saas.servlet;

import com.kabunx.component.common.util.PatternUtils;
import com.kabunx.component.saas.context.MultiTenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 通过二级域名解析出租户信息
 */
@Slf4j
@Order(1)
public class MultiTenantHandlerInterceptor implements AsyncHandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        // 获取域名
        String serverName = request.getServerName();
        // 解析出二级域名
        String subdomain = PatternUtils.getSubdomain(serverName);
        if (Objects.isNull(subdomain)) {
            subdomain = "default";
        }
        log.info("[SaaS] 当前租户为 - {}", subdomain);
        MultiTenantContextHolder.setTenantName(subdomain);
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) throws Exception {
        MultiTenantContextHolder.removeTenantName();
        AsyncHandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
