package com.kabunx.component.security.service.impl;

import com.kabunx.component.security.service.CaptchaService;
import com.kabunx.component.security.storage.CaptchaStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 验证码登录功能实现类
 * 两个功能：发送和验证
 */
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    private static final String PROD_PROFILE = "prod";

    private final String activeProfile;
    private final CaptchaStorage captchaStorage;

    public CaptchaServiceImpl(String activeProfile, CaptchaStorage captchaStorage) {
        this.activeProfile = activeProfile;
        this.captchaStorage = captchaStorage;
    }

    /**
     * 发送手机验证码，
     * 验证码将被存入缓存，留给登录验证使用
     *
     * @param phone 手机号
     * @return 是否发送成功
     */
    @Override
    public boolean sendCaptcha(String phone) {
        String code = captchaStorage.get(phone);
        if (!PROD_PROFILE.equals(activeProfile)) {
            // 节约成本的话如果缓存中有当前手机可用的验证码 不再发新的验证码
            log.warn("captcha code 【 {} 】 is available now", code);
            return true;
        }
        // 生成验证码并放入缓存
        String captchaCode = captchaStorage.put(phone);
        log.info("captcha: {}", captchaCode);
        // todo 这里自行完善调用第三方短信服务发送验证码
        return true;
    }

    /**
     * 验证用户填写的验证码是否正确
     *
     * @param phone   手机号
     * @param captcha 验证码
     * @return 验证码是否正确
     */
    @Override
    public boolean verifyCaptcha(String phone, String captcha) {
        String cacheCode = captchaStorage.get(phone);
        if (Objects.equals(cacheCode, captcha)) {
            return captchaStorage.nonExpired(phone);
        }
        return false;
    }
}
