package com.kabunx.component.common.exception;

/**
 * 业务异常
 */
public class BizException extends RuntimeException {
    private String code;

    private static final String DEFAULT_CODE = "E00000";

    public BizException(String errMessage) {
        super(errMessage);
        this.code = DEFAULT_CODE;
    }

    public BizException(ErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.code = errorInfo.getCode();
    }

    public BizException(String code, String errMessage) {
        super(errMessage);
        this.code = code;
    }

    public BizException(String errMessage, Throwable e) {
        super(errMessage, e);
        this.code = DEFAULT_CODE;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
