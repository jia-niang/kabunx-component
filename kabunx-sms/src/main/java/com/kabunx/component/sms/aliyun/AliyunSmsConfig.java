package com.kabunx.component.sms.aliyun;

import lombok.Data;

import java.io.Serializable;

@Data
public class AliyunSmsConfig implements Serializable {
    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
}
