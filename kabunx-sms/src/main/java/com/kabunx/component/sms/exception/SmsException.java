package com.kabunx.component.sms.exception;

import com.kabunx.component.common.exception.ErrorInfo;
import com.kabunx.component.common.exception.SysException;

public class SmsException extends SysException {
    public SmsException(String errMessage) {
        super(errMessage);
    }

    public SmsException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    public SmsException(String code, String errMessage) {
        super(code, errMessage);
    }

    public SmsException(String errMessage, Throwable e) {
        super(errMessage, e);
    }

    public SmsException(String errorCode, String errMessage, Throwable e) {
        super(errorCode, errMessage, e);
    }
}
