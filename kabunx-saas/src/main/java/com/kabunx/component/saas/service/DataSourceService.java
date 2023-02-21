package com.kabunx.component.saas.service;

import com.kabunx.component.common.exception.BizException;

public interface DataSourceService {

    /**
     * 初始化所有租户连接池
     */
    void initHikari();

    boolean initHikariByTenantName(String tenantName) throws BizException;
}
