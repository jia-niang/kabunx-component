package com.kabunx.component.saas.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public class MultiTenantContextHolder {
    private static final ThreadLocal<String> tenantContext = new TransmittableThreadLocal<>();

    public static void setTenantName(String name) {
        tenantContext.set(name);
    }

    public static String getTenantName() {
        return tenantContext.get();
    }

    public static void removeTenantName() {
        tenantContext.remove();
    }

}
