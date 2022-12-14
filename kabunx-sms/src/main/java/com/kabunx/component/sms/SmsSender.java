package com.kabunx.component.sms;

import com.kabunx.component.sms.dto.SmsSendEvent;

public interface SmsSender {

    String getSenderName();

    void doSend(SmsSendEvent smsSendEvent);
}
