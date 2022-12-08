package com.kabunx.component.core.exception;

/**
 * 系统异常
 */
public enum SysErrorInfo implements ErrorInfo {
    FORBIDDEN_ERROR("S01000", "拒绝访问"),
    ACCESS_UNAUTHORIZED("A01301", "访问未授权");

    private final String code;

    private final String message;

    SysErrorInfo(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
