package com.kabunx.component.saas;


import java.util.List;

public interface MultiTenantService {

    TenantConfig getConfigByName(String name);

    List<TenantConfig> list();
}
