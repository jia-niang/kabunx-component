package com.kabunx.component.saas.servlet;

import com.kabunx.component.saas.service.DataSourceService;
import com.kabunx.component.saas.context.DataSourceContextHolder;
import com.kabunx.component.saas.context.MultiTenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 多租户数据源切换
 */
@Slf4j
@Order(2) // 必须在域名解析之后
public class DataSourceHandlerInterceptor implements AsyncHandlerInterceptor {

    private final DataSourceService dataSourceService;

    public DataSourceHandlerInterceptor(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String tenantName = MultiTenantContextHolder.getTenantName();
        // 将新租户的数据源添加到动态数据源
        boolean isInit = dataSourceService.initHikariByTenantName(tenantName);
        if (isInit) {
            DataSourceContextHolder.setDataSourceKey(tenantName);
            log.info("[SaaS] 切换数据源到 {}", tenantName);
        }
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) throws Exception {
        log.info("[SaaS] 重置数据源");
        DataSourceContextHolder.removeDataSourceKey();
        AsyncHandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
