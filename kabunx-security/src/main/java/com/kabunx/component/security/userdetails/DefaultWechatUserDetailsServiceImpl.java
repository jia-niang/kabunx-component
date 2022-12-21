package com.kabunx.component.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class DefaultWechatUserDetailsServiceImpl implements WechatUserDetailsService {
    @Override
    public UserDetails decryptData(String code, String iv, String encryptedData) throws UsernameNotFoundException {
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_APP");
        return new Member(1L, "user", "wechat", "PASSWORD", roles);
    }
}
