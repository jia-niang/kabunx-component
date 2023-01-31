package com.kabunx.component.sms;

import com.kabunx.component.sms.dto.SendSmsEvent;

public interface SmsSender {

    String getSenderName();

    void doSend(SendSmsEvent sendSmsEvent);

}
