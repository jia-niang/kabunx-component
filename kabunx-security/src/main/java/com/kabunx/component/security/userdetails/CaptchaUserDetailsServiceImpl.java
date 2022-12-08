package com.kabunx.component.security.userdetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * 仅用于测试
 */
@Slf4j
public class CaptchaUserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        log.info("验证码登录");
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_APP");
        return new Member(1L, "hr", phone, "PASSWORD", roles);
    }
}
