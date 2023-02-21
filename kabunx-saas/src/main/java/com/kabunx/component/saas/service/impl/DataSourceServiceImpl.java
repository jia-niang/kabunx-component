package com.kabunx.component.saas.service.impl;

import com.kabunx.component.common.exception.BizException;
import com.kabunx.component.saas.SaaSErrorInfo;
import com.kabunx.component.saas.TenantConfig;
import com.kabunx.component.saas.context.DataSourceContextHolder;
import com.kabunx.component.saas.datasource.DynamicDataSource;
import com.kabunx.component.saas.service.DataSourceService;
import com.kabunx.component.saas.service.MultiTenantService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DataSourceServiceImpl implements DataSourceService {
    private final MultiTenantService multiTenantService;

    private final DynamicDataSource dynamicDataSource;

    public DataSourceServiceImpl(MultiTenantService multiTenantService, DynamicDataSource dynamicDataSource) {
        this.multiTenantService = multiTenantService;
        this.dynamicDataSource = dynamicDataSource;
    }

    @Override
    public void initHikari() {
        log.info("初始化多数据源");
        List<TenantConfig> tenants = multiTenantService.list();
        if (CollectionUtils.isEmpty(tenants)) {
            return;
        }
        for (TenantConfig tenantConfig : tenants) {
            if (DataSourceContextHolder.containDataSourceKey(tenantConfig.getName())) {
                continue;
            }
            initHikariByTenantConfig(tenantConfig);
        }
    }

    @Override
    public boolean initHikariByTenantName(String tenantName) throws BizException {
        if (DataSourceContextHolder.containDataSourceKey(tenantName)) {
            return true;
        }
        TenantConfig tenantConfig = multiTenantService.getConfigByName(tenantName);
        if (Objects.isNull(tenantConfig)) {
            log.error("[SaaS] 租户 - {}，配置信息不存在", tenantName);
            throw new BizException(SaaSErrorInfo.TENANT_ERROR);
        }
        return initHikariByTenantConfig(tenantConfig);
    }


    /**
     * 初始化连接池
     *
     * @param tenantConfig 租户配置信息
     * @return 是否成功
     */
    private boolean initHikariByTenantConfig(@NonNull TenantConfig tenantConfig) {
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
            dynamicDataSource.addDataSource(tenantConfig.getName(), hikariDataSource);
            return true;
        } catch (Exception ex) {
            log.error("[SaaS] 租户 - {}，连接池创建失败", tenantConfig.getName(), ex);
            return false;
        }
    }

}
