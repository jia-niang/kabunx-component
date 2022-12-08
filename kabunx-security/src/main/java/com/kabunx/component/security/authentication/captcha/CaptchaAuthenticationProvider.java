package com.kabunx.component.security.authentication.captcha;

import com.kabunx.component.security.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;

@Slf4j
public class CaptchaAuthenticationProvider implements AuthenticationProvider {

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final UserDetailsService userDetailsService;
    private final CaptchaService captchaService;

    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public CaptchaAuthenticationProvider(UserDetailsService userDetailsService, CaptchaService captchaService) {
        this.userDetailsService = userDetailsService;
        this.captchaService = captchaService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(CaptchaAuthenticationToken.class, authentication, () -> messages.getMessage(
                "CaptchaAuthenticationProvider.onlySupports",
                "Only   is supported")
        );
        CaptchaAuthenticationToken unAuthenticationToken = (CaptchaAuthenticationToken) authentication;
        String phone = unAuthenticationToken.getName();
        String captcha = (String) unAuthenticationToken.getCredentials();
        UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
        // 此处省略对UserDetails 的可用性 是否过期 是否锁定 是否失效的检验 建议根据实际
        // 情况添加 或者在 UserDetailsService 的实现中处理
        if (Objects.isNull(userDetails)) {
            throw new BadCredentialsException("Bad credentials");
        }
        // 验证码校验
        if (captchaService.verifyCaptcha(phone, captcha)) {
            return createSuccessAuthentication(authentication, userDetails);
        } else {
            throw new BadCredentialsException("captcha is not matched");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CaptchaAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 认证成功将非授信凭据转为授信凭据.
     * 封装用户信息 角色信息。
     *
     * @param authentication the authentication
     * @param user           the user
     * @return the authentication
     */
    protected Authentication createSuccessAuthentication(Authentication authentication, UserDetails user) {
        Collection<? extends GrantedAuthority> authorities = authoritiesMapper.mapAuthorities(user.getAuthorities());
        CaptchaAuthenticationToken authenticationToken = new CaptchaAuthenticationToken(user, null, authorities);
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

}
