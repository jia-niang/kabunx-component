package com.kabunx.component.security.web.authentication.wechat;

import com.kabunx.component.security.userdetails.WechatUserDetailsService;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;

public class WechatAuthenticationProvider implements AuthenticationProvider {
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private final WechatUserDetailsService wechatUserDetailsService;

    public WechatAuthenticationProvider(WechatUserDetailsService wechatUserDetailsService) {
        this.wechatUserDetailsService = wechatUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(WechatAuthenticationToken.class, authentication, () -> messages.getMessage(
                "WechatAuthenticationToken.onlySupports",
                "Only CaptchaAuthenticationToken is supported")
        );
        WechatAuthenticationToken unAuthenticationToken = (WechatAuthenticationToken) authentication;
        String code = unAuthenticationToken.getCode();
        String iv = unAuthenticationToken.getIv();
        String data = unAuthenticationToken.getEncryptedData();
        UserDetails userDetails = wechatUserDetailsService.decryptData(code, iv, data);
        if (Objects.nonNull(userDetails)) {
            return createSuccessAuthentication(authentication, userDetails);
        } else {
            throw new BadCredentialsException("captcha is not matched");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }

    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection<? extends GrantedAuthority> authorities = authoritiesMapper.mapAuthorities(user.getAuthorities());
        WechatAuthenticationToken authenticationToken = new WechatAuthenticationToken(user, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }
}
