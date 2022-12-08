package com.kabunx.component.security.service;

public interface CaptchaService {
    boolean sendCaptcha(String phone);

    boolean verifyCaptcha(String phone, String captcha);

}
