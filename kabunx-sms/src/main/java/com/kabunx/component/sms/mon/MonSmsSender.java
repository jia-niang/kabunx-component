package com.kabunx.component.sms.mon;

import com.kabunx.component.common.util.HttpUtils;
import com.kabunx.component.common.util.JsonUtils;
import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.enums.SenderEnum;
import com.kabunx.component.sms.dto.SendSmsEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class MonSmsSender implements SmsSender {
    private final MonSmsConfig smsConfig;

    public MonSmsSender(MonSmsConfig smsConfig) {
        this.smsConfig = smsConfig;
    }

    @Override
    public String getSenderName() {
        return SenderEnum.MON.getName();
    }

    @Override
    public void doSend(SendSmsEvent sendSmsEvent) {
        Message message = buildMessage();
        try {
            message.setMobile(sendSmsEvent.getPhone());
            message.setContent(URLEncoder.encode(sendSmsEvent.getContent(), "GBK"));
            HttpUtils.doPost(smsConfig.getUrls().get(0), message.toJson(), s -> {
                MonResponse response = JsonUtils.json2Object(s, MonResponse.class);
                response.doLog();
            });
        } catch (Exception ex) {
            log.error("梦网短信发送失败", ex);
        }
    }

    private Message buildMessage() {
        final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss"));
        Message message = new Message();
        if (StringUtils.isEmpty(smsConfig.getKey())) {
            message.setUserid(smsConfig.getUserId());
            message.setPwd(smsConfig.getMD5Pwd(timestamp));
        } else {
            message.setApikey(smsConfig.getKey());
        }
        message.setTimestamp(timestamp);
        return message;
    }
}
