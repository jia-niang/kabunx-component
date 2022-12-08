package com.kabunx.component.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 用于微信小程序授权登录验证
 */
public interface WechatUserDetailsService {
    UserDetails decryptData(String code, String iv, String encryptedData) throws UsernameNotFoundException;
}
