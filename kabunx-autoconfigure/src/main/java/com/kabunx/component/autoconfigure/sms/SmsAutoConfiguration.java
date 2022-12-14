package com.kabunx.component.autoconfigure.sms;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.aliyun.AliyunSmsSender;
import com.kabunx.component.sms.context.SmsSenderContext;
import com.kabunx.component.sms.mon.MonSmsSender;
import com.kabunx.component.sms.tencent.TencentSmsSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

    @Bean
    @ConditionalOnClass(SmsSenderContext.class)
    SmsSenderContext smsSenderContext() {
        return new SmsSenderContext();
    }

    @Bean("aliyunSmsSender")
    @ConditionalOnClass(AliyunSmsSender.class)
    @ConditionalOnProperty(name = "sms.aliyun.enabled", havingValue = "true")
    SmsSender aliyunSmsSender() {
        return new AliyunSmsSender();
    }

    @Bean("tencentSmsSender")
    @ConditionalOnClass(TencentSmsSender.class)
    @ConditionalOnProperty(name = "sms.tencent.enabled", havingValue = "true")
    SmsSender tencentSmsSendHandler() {
        return new TencentSmsSender();
    }

    @Bean("monSmsSender")
    @ConditionalOnClass(MonSmsSender.class)
    @ConditionalOnProperty(name = "sms.mon.enabled", havingValue = "true")
    SmsSender monSmsSender() {
        return new MonSmsSender();
    }
}
