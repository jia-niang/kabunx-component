package com.kabunx.component.common.constant;

public interface SecurityConstants {
    /**
     * 黑名单token前缀
     */
    String TOKEN_BLACKLIST_PREFIX = "auth:token:blacklist:";


    String USERNAME_KEY = "username";

    String AUTHENTICATION_PREFIX = "Bearer ";

    /**
     * 认证身份标识
     */
    String AUTHENTICATION_IDENTITY_KEY = "authenticationIdentity";

    /**
     * 未定义的客户端
     */
    String UNKNOWN_CLIENT_ID = "unknown";

    /**
     * 接口文档 Knife4j 测试客户端ID
     */
    String TEST_CLIENT_ID = "test";

    String WEB_CLIENT_ID = "web";

    /**
     * 后台客户端ID
     */
    String ADMIN_CLIENT_ID = "admin";

    /**
     * 移动端（H5/Android/IOS）客户端ID
     */
    String APP_CLIENT_ID = "app";

    /**
     * 微信小程序客户端ID
     */
    String WECHAT_CLIENT_ID = "wechat";
}
