package com.kabunx.component.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class DefaultCaptchaUserDetailsServiceImpl implements CaptchaUserDetailsService {
    @Override
    public UserDetails loadUserByCaptcha(String phone, String captcha, String type) throws UsernameNotFoundException {
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_APP");
        return new Member(1L, "hr", phone, "PASSWORD", roles);
    }
}
