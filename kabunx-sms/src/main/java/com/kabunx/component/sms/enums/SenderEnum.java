package com.kabunx.component.sms.enums;

public enum SenderEnum {
    ALIYUN("aliyun", "阿里云"),
    TENCENT("tencent", "腾讯云"),
    MON("mon", "梦网");
    private final String name;
    private final String desc;

    SenderEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
