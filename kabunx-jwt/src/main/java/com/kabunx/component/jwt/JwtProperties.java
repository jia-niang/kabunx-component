package com.kabunx.component.jwt;

import lombok.Data;

@Data
public class JwtProperties {
    public static final String JWT_PREFIX = "security.jwt";

    private static final String DEFAULT_ISS = "kabunx";
    private static final String DEFAULT_SUB = "web";

    private static final Integer ACCESS_EXP_DAYS = 30;

    /**
     * 是否可用
     */
    private boolean enabled;

    /**
     * jks 路径
     */
    private String keyLocation;

    /**
     * key alias
     */
    private String keyAlias;

    /**
     * key store pass
     */
    private String keyPass;

    /**
     * jwt签发者
     */
    private String iss = DEFAULT_ISS;

    /**
     * jwt所面向的用户
     */
    private String sub = DEFAULT_SUB;

    /**
     * access jwt token 有效天数
     */
    private int accessExpDays = ACCESS_EXP_DAYS;

    /**
     * refresh jwt token 有效天数
     */
    private int refreshExpDays;
}
