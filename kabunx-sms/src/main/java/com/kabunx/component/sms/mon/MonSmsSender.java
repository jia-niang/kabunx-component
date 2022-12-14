package com.kabunx.component.sms.mon;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.enums.SenderEnum;
import com.kabunx.component.sms.dto.SmsSendEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MonSmsSender implements SmsSender {
    @Override
    public String getSenderName() {
        return SenderEnum.MON.getName();
    }

    @Override
    public void doSend(SmsSendEvent smsSendEvent) {
        log.info("梦网短信发送");
    }
}
