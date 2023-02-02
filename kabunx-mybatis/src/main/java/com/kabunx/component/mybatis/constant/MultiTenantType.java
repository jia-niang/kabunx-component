package com.kabunx.component.mybatis.constant;


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
     * 字段模式
     * 在sql中拼接 tenant_code 字段
     */
    COLUMN("字段模式"),
    /**
     * 独立schema模式
     * 在sql中拼接 数据库 schema
     * <p>
     * 该模式暂不支持复杂sql、存储过程、函数等，欢迎大家提供解决方案。
     */
    SCHEMA("独立schema模式"),
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
