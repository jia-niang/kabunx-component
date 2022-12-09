package com.kabunx.component.mybatis.exception;

import com.kabunx.component.common.exception.ErrorInfo;

public class ModelExistedException extends ModelException {

    public ModelExistedException(String errMessage) {
        super(errMessage);
    }

    public ModelExistedException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    public ModelExistedException(String code, String errMessage) {
        super(code, errMessage);
    }

    public ModelExistedException(String errMessage, Throwable e) {
        super(errMessage, e);
    }
}
