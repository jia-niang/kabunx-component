package com.kabunx.component.autoconfigure.sms;

import com.kabunx.component.sms.aliyun.AliyunSmsProperties;
import com.kabunx.component.sms.mon.MonSmsProperties;
import com.kabunx.component.sms.tencent.TencentSmsProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SmsProperties.PREFIX)
public class SmsProperties {
    public static final String PREFIX = "sms";

    private AliyunSmsProperties aliyun;

    private TencentSmsProperties tencent;

    private MonSmsProperties mon;
}
