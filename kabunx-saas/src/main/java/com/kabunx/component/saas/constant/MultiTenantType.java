package com.kabunx.component.saas.constant;


import lombok.Getter;

import java.util.Objects;

/**
 * 多租户类型， NONE、COLUMN、SCHEMA、DATASOURCE
 */
@Getter
public enum MultiTenantType {
    /**
     * 非租户模式
     */
    NONE("非租户模式"),
    /**
     * 独立数据源模式
     */
    DATASOURCE("独立数据源模式");

    private final String description;

    MultiTenantType(String description) {
        this.description = description;
    }

    public boolean eq(String name) {
        return this.name().equalsIgnoreCase(name);
    }

    public boolean eq(MultiTenantType type) {
        return Objects.nonNull(type) && eq(type.name());
    }
}
