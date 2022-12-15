package com.kabunx.component.sms.tencent;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.enums.SenderEnum;
import com.kabunx.component.sms.dto.SendSmsEvent;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TencentSmsSender implements SmsSender {
    private final TencentSmsConfig smsConfig;

    private final SmsClient client;

    public TencentSmsSender(TencentSmsConfig smsConfig) {
        this.smsConfig = smsConfig;
        client = buildClient();
    }

    @Override
    public String getSenderName() {
        return SenderEnum.TENCENT.getName();
    }

    @Override
    public void doSend(SendSmsEvent sendSmsEvent) {
        SendSmsRequest request = new SendSmsRequest();
        // 短信应用ID
        request.setSmsSdkAppId(smsConfig.getSdkAppId());
        // 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
        request.setSignName(sendSmsEvent.getSignName());
        request.setTemplateId(sendSmsEvent.getTemplateCode());
        String[] paramSet = sendSmsEvent.getTemplateParams().values().toArray(new String[0]);
        request.setTemplateParamSet(paramSet);
        String[] phoneSet = {sendSmsEvent.getPhone()};
        request.setPhoneNumberSet(phoneSet);
        try {
            SendSmsResponse response = client.SendSms(request);
            log.info("Tencent sms response is {}", response);
        } catch (TencentCloudSDKException ex) {
            log.error("腾讯云短信发送失败", ex);
        }
    }

    private SmsClient buildClient() {
        Credential credential = new Credential(smsConfig.getSecretId(), smsConfig.getSecretKey());
        return new SmsClient(credential, smsConfig.getRegionId());
    }
}
