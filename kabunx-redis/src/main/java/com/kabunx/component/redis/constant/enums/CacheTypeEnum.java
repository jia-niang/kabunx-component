package com.kabunx.component.redis.constant.enums;

public enum CacheTypeEnum {
    CAPTCHA("captcha:", 600, "图形验证码"),
    SMS_CODE("sms_code:", 600, "短信验证码"),
    CLIENT_IP("client_ip:", 6000, "客户端IP");
    private final String prefix;
    private final int seconds;

    private final String description;

    CacheTypeEnum(String prefix, int seconds, String description) {
        this.prefix = prefix;
        this.seconds = seconds;
        this.description = description;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getSeconds() {
        return seconds;
    }

    public String getDescription() {
        return description;
    }
}
