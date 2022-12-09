package com.kabunx.component.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 手机号验证码登录
 */
public interface CaptchaUserDetailsService {
    UserDetails loadUserByCaptcha(String phone, String captcha, String type) throws UsernameNotFoundException;
}
