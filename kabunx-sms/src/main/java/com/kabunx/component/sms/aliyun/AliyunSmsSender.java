package com.kabunx.component.sms.aliyun;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.enums.SenderEnum;
import com.kabunx.component.sms.dto.SmsSendEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliyunSmsSender implements SmsSender {
    @Override
    public String getSenderName() {
        return SenderEnum.ALIYUN.getName();
    }

    @Override
    public void doSend(SmsSendEvent smsSendEvent) {
        log.info("阿里云短信发送");
    }
}
