package com.kabunx.component.core.exception;

public enum BizErrorInfo implements ErrorInfo {
    DEFAULT_ERROR("B00001", "业务异常"),
    VALIDATOR_ERROR("B01001", "数据验证错误");

    private final String code;

    private final String message;

    BizErrorInfo(String code, String message) {
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
