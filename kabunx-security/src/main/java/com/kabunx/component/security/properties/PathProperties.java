package com.kabunx.component.security.properties;

import lombok.Data;

import java.util.List;

@Data
public class PathProperties {
    static final String PATH_PREFIX = "security.path";

    /**
     * 匿名路由
     */
    private List<String> anonymous;

    /**
     * 用户访问路由白名单
     */
    private List<String> whitelist;

    /**
     * 任务脚本路由列表
     */
    private List<String> scriptList;

    /**
     * 任务脚本路由列表，所对应的ip地址
     */
    private String scriptIpaddress;
}
