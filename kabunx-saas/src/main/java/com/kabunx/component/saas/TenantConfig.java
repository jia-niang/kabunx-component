package com.kabunx.component.saas;


import lombok.Data;

@Data
public class TenantConfig {

    private String name;

    private String dbPoolName;

    private String dbDriverClassName;

    private String dbJdbcUrl;

    private String dbUsername;

    private String dbPassword;

}
