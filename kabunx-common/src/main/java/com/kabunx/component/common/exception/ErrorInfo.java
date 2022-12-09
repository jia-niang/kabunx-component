package com.kabunx.component.common.exception;

/**
 * 错误信息
 */
public interface ErrorInfo {
    /**
     * 获取错误码
     *
     * @return 错误码
     */
    String getCode();

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getMessage();
}
