package com.kabunx.component.autoconfigure.security;

import com.kabunx.component.security.userdetails.CaptchaUserDetailsService;
import com.kabunx.component.security.userdetails.WechatUserDetailsService;
import com.kabunx.component.security.web.authentication.captcha.CaptchaAuthenticationProvider;
import com.kabunx.component.security.web.authentication.wechat.WechatAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityProviderAutoConfiguration {

    @Bean
    @ConditionalOnClass(CaptchaAuthenticationProvider.class)
    @ConditionalOnBean(CaptchaUserDetailsService.class)
    CaptchaAuthenticationProvider captchaAuthenticationProvider(CaptchaUserDetailsService userDetailsService) {
        return new CaptchaAuthenticationProvider(userDetailsService);
    }

    @Bean
    @ConditionalOnClass(WechatAuthenticationProvider.class)
    @ConditionalOnBean(WechatUserDetailsService.class)
    WechatAuthenticationProvider wechatAuthenticationProvider(WechatUserDetailsService userDetailsService) {
        return new WechatAuthenticationProvider(userDetailsService);
    }
}
