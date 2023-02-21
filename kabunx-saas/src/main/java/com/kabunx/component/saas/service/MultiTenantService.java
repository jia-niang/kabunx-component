package com.kabunx.component.saas.service;


import com.kabunx.component.saas.TenantConfig;

import java.util.List;

public interface MultiTenantService {

    TenantConfig getConfigByName(String name);

    List<TenantConfig> list();
}
