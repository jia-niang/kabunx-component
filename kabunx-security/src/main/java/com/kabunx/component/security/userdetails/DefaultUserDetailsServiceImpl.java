package com.kabunx.component.security.userdetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 * 并没有具体实现，可用于测试
 */
@Slf4j
public class DefaultUserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("用户名登录");
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_APP");
        return new Member(1L, "hr", username, "PASSWORD", roles);
    }
}
