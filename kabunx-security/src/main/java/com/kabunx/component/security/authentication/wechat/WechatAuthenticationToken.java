package com.kabunx.component.security.authentication.wechat;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

public class WechatAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 来自于微信code，用于解码
     */
    private final Object principal;
    private final String code;
    private final String iv;
    private final String encryptedData;

    /**
     * 账号校验之前的token构建
     *
     * @param code the code
     */
    public WechatAuthenticationToken(String code, String iv, String encryptedData) {
        super(null);
        this.principal = null;
        this.code = code;
        this.iv = iv;
        this.encryptedData = encryptedData;
        setAuthenticated(false);
    }

    /**
     * 账号校验成功之后的token构建
     *
     * @param principal   the principal
     * @param authorities the authorities
     */
    public WechatAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.code = null;
        this.iv = null;
        this.encryptedData = null;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getCode() {
        return code;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
