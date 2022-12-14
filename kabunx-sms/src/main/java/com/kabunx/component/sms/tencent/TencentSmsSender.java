package com.kabunx.component.sms.tencent;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.enums.SenderEnum;
import com.kabunx.component.sms.dto.SmsSendEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TencentSmsSender implements SmsSender {
    @Override
    public String getSenderName() {
        return SenderEnum.TENCENT.getName();
    }

    @Override
    public void doSend(SmsSendEvent smsSendEvent) {
        log.info("腾讯云短信发送");
    }
}
