package com.kabunx.component.autoconfigure.sms;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.aliyun.AliyunSmsConfig;
import com.kabunx.component.sms.aliyun.AliyunSmsSender;
import com.kabunx.component.sms.context.SmsSenderHolder;
import com.kabunx.component.sms.mon.MonSmsConfig;
import com.kabunx.component.sms.mon.MonSmsSender;
import com.kabunx.component.sms.tencent.TencentSmsConfig;
import com.kabunx.component.sms.tencent.TencentSmsSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SmsSenderHolder.class)
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

    private final SmsProperties smsProperties;

    public SmsAutoConfiguration(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Bean
    SmsSenderHolder smsSenderContextHolder() {
        return new SmsSenderHolder();
    }

    @Bean("aliyunSmsSender")
    @ConditionalOnClass(AliyunSmsSender.class)
    SmsSender aliyunSmsSender() {
        return new AliyunSmsSender(aliyunSmsConfig(smsProperties.getAliyun()));
    }

    @Bean("tencentSmsSender")
    @ConditionalOnClass(TencentSmsSender.class)
    SmsSender tencentSmsSendHandler() {
        return new TencentSmsSender(tencentSmsConfig(smsProperties.getTencent()));
    }

    @Bean("monSmsSender")
    @ConditionalOnClass(MonSmsSender.class)
    SmsSender monSmsSender() {
        return new MonSmsSender(monSmsConfig(smsProperties.getMon()));
    }


    /**
     * @param aliyun 配置
     * @return 内部配置
     */
    private AliyunSmsConfig aliyunSmsConfig(SmsProperties.Aliyun aliyun) {
        AliyunSmsConfig config = new AliyunSmsConfig();
        config.setRegionId(aliyun.getRegionId());
        config.setAccessKeyId(aliyun.getAccessKeyId());
        config.setAccessKeySecret(aliyun.getAccessKeySecret());
        return config;
    }

    private TencentSmsConfig tencentSmsConfig(SmsProperties.Tencent tencent) {
        TencentSmsConfig config = new TencentSmsConfig();
        config.setSecretId(tencent.getSecretId());
        config.setSecretKey(tencent.getSecretKey());
        return config;
    }

    private MonSmsConfig monSmsConfig(SmsProperties.Mon mon) {
        MonSmsConfig config = new MonSmsConfig();
        config.setUserId(config.getUserId());
        config.setPwd(config.getPwd());
        config.setKey(config.getKey());
        config.setUrls(mon.getUrls());
        return config;
    }

}
