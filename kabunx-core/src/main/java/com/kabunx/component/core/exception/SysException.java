package com.kabunx.component.core.exception;

/**
 * 系统异常
 */
public class SysException extends RuntimeException {
    private String code;

    private static final String DEFAULT_CODE = "E00000";

    public SysException(String errMessage) {
        super(errMessage);
        this.code = DEFAULT_CODE;
    }

    public SysException(ErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.code = errorInfo.getCode();
    }

    public SysException(String code, String errMessage) {
        super(errMessage);
        this.code = code;
    }

    public SysException(String errMessage, Throwable e) {
        super(errMessage, e);
        this.code = DEFAULT_CODE;
    }

    public SysException(String errorCode, String errMessage, Throwable e) {
        super(errMessage, e);
        this.code = errorCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
