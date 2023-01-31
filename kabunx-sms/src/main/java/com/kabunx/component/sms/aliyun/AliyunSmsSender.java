package com.kabunx.component.sms.aliyun;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.kabunx.component.common.util.JsonUtils;
import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.enums.SenderEnum;
import com.kabunx.component.sms.dto.SendSmsEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliyunSmsSender implements SmsSender {

    private final AliyunSmsConfig smsConfig;

    private final IAcsClient client;

    public AliyunSmsSender(AliyunSmsConfig smsConfig) {
        this.smsConfig = smsConfig;
        client = buildClient(smsConfig);
    }

    @Override
    public String getSenderName() {
        return SenderEnum.ALIYUN.getName();
    }

    @Override
    public void doSend(SendSmsEvent sendSmsEvent) {
        CommonRequest request = new CommonRequest();
        request.putQueryParameter("PhoneNumbers", sendSmsEvent.getPhone());
        request.putQueryParameter("SignName", sendSmsEvent.getSignName());
        request.putQueryParameter("TemplateCode", sendSmsEvent.getTemplateCode());
        String json = JsonUtils.object2Json(sendSmsEvent.getTemplateParams());
        request.putQueryParameter("putQueryParameter", json);
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("[SMS] Aliyun sms response data is - {}", response.getData());
        } catch (ClientException ex) {
            log.error("[SMS] 阿里云短信发送失败", ex);
        }
    }

    private IAcsClient buildClient(AliyunSmsConfig config) {
        DefaultProfile profile = DefaultProfile.getProfile(
                smsConfig.getRegionId(),
                smsConfig.getAccessKeyId(),
                smsConfig.getAccessKeySecret());
        return new DefaultAcsClient(profile);
    }
}
