package com.kabunx.component.autoconfigure.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = SecurityProperties.PREFIX, ignoreInvalidFields = true)
public class SecurityProperties {
    public static final String PREFIX = "security";

    /**
     * JWT相关配置
     */
    Jwt jwt;

    /**
     * 路由配置信息
     */
    AntPath path;

    @Data
    public static class Jwt {
        /**
         * jwt签发者
         */
        private String iss;

        /**
         * jwt所面向的用户
         */
        private String sub;

        /**
         * access jwt token 有效天数
         */
        private int accessExpDays;

        /**
         * refresh jwt token 有效天数
         */
        private int refreshExpDays;
    }

    @Data
    public static class AntPath {
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

}
