package com.kabunx.component.sms;

import com.aliyuncs.exceptions.ClientException;
import com.kabunx.component.sms.dto.SendSmsEvent;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

public interface SmsSender {

    String getSenderName();

    void doSend(SendSmsEvent sendSmsEvent) throws ClientException, TencentCloudSDKException;
}
