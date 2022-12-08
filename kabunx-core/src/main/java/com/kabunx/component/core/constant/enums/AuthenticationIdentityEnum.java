package com.kabunx.component.core.constant.enums;

/**
 * 认证身份标识枚举
 */
public enum AuthenticationIdentityEnum implements BaseEnum<String> {
    USERNAME("username", "用户名"),
    MOBILE("captcha", "验证码"),
    WECHAT("wechat", "微信"),
    OPENID("openId", "开放式认证系统唯一身份标识");

    private final String value;

    private final String label;

    AuthenticationIdentityEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
