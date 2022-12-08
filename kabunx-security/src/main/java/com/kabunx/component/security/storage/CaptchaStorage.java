package com.kabunx.component.security.storage;

/**
 * 验证码存储接口
 */
public interface CaptchaStorage {
    /**
     * 验证码放入缓存.
     *
     * @param phone the phone
     * @return the string
     */
    String put(String phone);

    /**
     * 从缓存取验证码.
     *
     * @param phone the phone
     * @return the string
     */
    String get(String phone);

    /**
     * 验证码手动过期.
     *
     * @param phone the phone
     */
    boolean isExpired(String phone);

    boolean nonExpired(String phone);

}
