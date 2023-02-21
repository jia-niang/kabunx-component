package com.kabunx.component.saas;

import com.kabunx.component.common.exception.ErrorInfo;

/**
 * 租户错误信息
 */
public enum SaaSErrorInfo implements ErrorInfo {
    DATASOURCE_UNDEFINED("S02001", "未定义数据源"),
    DATASOURCE_NOT_FOUND("S02002", "数据源不存在"),
    TENANT_ERROR("S02003", "租户信息错误");

    private final String code;

    private final String message;

    SaaSErrorInfo(String code, String message) {
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
