package com.kabunx.component.sms.tencent;

import lombok.Data;

import java.io.Serializable;

@Data
public class TencentSmsConfig implements Serializable {
    private Boolean enabled;
    private String regionId;
    private String secretId;
    private String secretKey;
    /**
     * 短信应用ID
     */
    private String sdkAppId;
}
