package com.kabunx.component.autoconfigure.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = SmsProperties.PREFIX, ignoreInvalidFields = true)
public class SmsProperties {
    public static final String PREFIX = "sms";

    /**
     * 阿里云短信配置
     */
    private Aliyun aliyun;

    /**
     * 腾讯云短信配置
     */
    private Tencent tencent;

    /**
     * 梦网短信配置
     */
    private Mon mon;

    @Data
    public static class Aliyun {
        /**
         * 是否开启
         */
        private Boolean enabled;
        /**
         * API支持的地域ID，比如：cn-hangzhou
         */
        private String regionId;
        /**
         * 您的AccessKey ID
         */
        private String accessKeyId;
        /**
         * 您的AccessKey Secret
         */
        private String accessKeySecret;
    }

    @Data
    public static class Tencent {
        /**
         * 是否开启
         */
        private Boolean enabled;
        /**
         * API支持的地域ID，比如：cn-hangzhou
         */
        private String regionId;
        /**
         * API 调用者的身份
         */
        private String secretId;
        /**
         * 用于加密签名字符串，需妥善保管，避免泄露
         */
        private String secretKey;
    }

    @Data
    public static class Mon {
        /**
         * 是否开启
         */
        private Boolean enabled;
        /**
         * 账号
         */
        private String uid;
        /**
         * 密码
         */
        private String pwd;

        /**
         * 用户唯一标识，32位长度，如存在uid/pwd可不填
         */
        private String key;

        private List<String> urls;
    }
}
