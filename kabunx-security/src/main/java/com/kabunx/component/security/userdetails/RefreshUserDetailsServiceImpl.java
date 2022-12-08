package com.kabunx.component.security.userdetails;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

public class RefreshUserDetailsServiceImpl<T extends Authentication> implements AuthenticationUserDetailsService<T>, InitializingBean {

    private Map<String, UserDetailsService> userDetailsServiceMap;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public UserDetails loadUserDetails(T token) throws UsernameNotFoundException {
        return null;
    }
}
