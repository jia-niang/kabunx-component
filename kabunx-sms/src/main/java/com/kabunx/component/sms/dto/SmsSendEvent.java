package com.kabunx.component.sms.dto;

import com.kabunx.component.common.dto.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(title = "短信发送时间")
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsSendEvent extends Event {

    @Schema(title = "谁来发送短信")
    private String senderName;

    @Schema(title = "手机号")
    private String phone;

    @Schema(title = "发送内容")
    private String content;
}
