package com.kabunx.component.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 根据自己的业务添加属性
 */
public class Member extends User {

    private final Long id;

    private final String type;

    public Member(Long id, String type, String username, String password,
                  Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.type = type;
    }

    public Member(Long id, String type, String username, String password,
                  boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                  Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
