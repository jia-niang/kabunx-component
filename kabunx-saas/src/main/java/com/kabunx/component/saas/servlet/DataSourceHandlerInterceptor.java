package com.kabunx.component.saas.servlet;

import com.kabunx.component.common.exception.BizException;
import com.kabunx.component.saas.MultiTenantService;
import com.kabunx.component.saas.TenantConfig;
import com.kabunx.component.saas.context.DataSourceContextHolder;
import com.kabunx.component.saas.context.MultiTenantContextHolder;
import com.kabunx.component.saas.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Objects;


/**
 * 多租户数据源切换
 */
@Slf4j
@Order(2) // 必须在域名解析之后
public class DataSourceHandlerInterceptor implements AsyncHandlerInterceptor {

    private final MultiTenantService multiTenantService;

    private final DynamicDataSource dynamicDataSource;

    public DataSourceHandlerInterceptor(MultiTenantService multiTenantService, DynamicDataSource dynamicDataSource) {
        this.multiTenantService = multiTenantService;
        this.dynamicDataSource = dynamicDataSource;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String tenantName = MultiTenantContextHolder.getTenantName();
        log.info("[SaaS] 切换数据源到 {}", tenantName);
        // 将新租户的数据源添加到动态数据源
        if (!DataSourceContextHolder.containDataSourceKey(tenantName)) {
            TenantConfig tenantConfig = multiTenantService.getConfigByName(tenantName);
            if (Objects.isNull(tenantConfig)) {
                log.error("[SaaS] 租户 - {}，配置信息不存在", tenantName);
                throw new BizException("租户未被初始化，请联系管理员");
            }
            DataSourceBuilder<? extends DataSource> dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName(tenantConfig.getDbDriverClassName());
            dataSourceBuilder.url(tenantConfig.getDbJdbcUrl());
            dataSourceBuilder.username(tenantConfig.getDbUsername());
            dataSourceBuilder.password(tenantConfig.getDbPassword());
            DataSource source = dataSourceBuilder.build();

            // 使用 Hikari 连接池
            HikariConfig config = new HikariConfig();
            config.setDataSource(source);
            config.setDriverClassName(tenantConfig.getDbDriverClassName());
            config.setJdbcUrl(tenantConfig.getDbJdbcUrl());
            config.setUsername(tenantConfig.getDbUsername());
            config.setPassword(tenantConfig.getDbPassword());
            config.setPoolName(tenantConfig.getDbPoolName());
            try (HikariDataSource hikariDataSource = new HikariDataSource(config)) {
                dynamicDataSource.addDataSource(tenantName, hikariDataSource);
                DataSourceContextHolder.setDataSourceKey(tenantName);
                log.info(DataSourceContextHolder.getDataSourceKey());
            } catch (Exception ex) {
                log.error("[SaaS] 租户 - {}，连接池创建失败", tenantName, ex);
            }
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
