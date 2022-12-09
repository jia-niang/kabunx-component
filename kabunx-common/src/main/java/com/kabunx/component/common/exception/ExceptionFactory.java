package com.kabunx.component.common.exception;

/**
 * 异常工厂类
 */
public class ExceptionFactory {
    public static BizException bizException(String errMessage) {
        return new BizException(errMessage);
    }

    public static BizException bizException(String errCode, String errMessage) {
        return new BizException(errCode, errMessage);
    }

    public static SysException sysException(String errMessage) {
        return new SysException(errMessage);
    }

    public static SysException sysException(String errCode, String errMessage) {
        return new SysException(errCode, errMessage);
    }

    public static SysException sysException(String errMessage, Throwable e) {
        return new SysException(errMessage, e);
    }

    public static SysException sysException(String errCode, String errMessage, Throwable e) {
        return new SysException(errCode, errMessage, e);
    }
}
