package com.kabunx.component.security.storage.impl;

import com.kabunx.component.security.storage.CaptchaStorage;

/**
 * 验证码存储实现类
 */
public class CaptchaStorageImpl implements CaptchaStorage {
    @Override
    public String put(String phone) {
        return null;
    }

    @Override
    public String get(String phone) {
        return "123123";
    }

    @Override
    public boolean isExpired(String phone) {
        return false;
    }

    @Override
    public boolean nonExpired(String phone) {
        return !isExpired(phone);
    }
}
