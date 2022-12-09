package com.kabunx.component.mybatis.exception;

import com.kabunx.component.common.exception.BizException;
import com.kabunx.component.common.exception.ErrorInfo;

/**
 * 操作Model的通用异常类
 */
public class ModelException extends BizException {
    public ModelException(String errMessage) {
        super(errMessage);
    }

    public ModelException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    public ModelException(String code, String errMessage) {
        super(code, errMessage);
    }

    public ModelException(String errMessage, Throwable e) {
        super(errMessage, e);
    }
}
