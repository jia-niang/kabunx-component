package com.kabunx.component.sms.dto;

import com.kabunx.component.common.dto.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Schema(title = "短信发送时间")
@Data
@EqualsAndHashCode(callSuper = false)
public class SendSmsEvent extends Event {

    @Schema(title = "谁来发送短信")
    private String senderName;

    @Schema(title = "接收短信的手机号码")
    private String phone;

    @Schema(title = "短信签名名称")
    private String signName;

    @Schema(title = "短信模板ID")
    private String templateCode;

    @Schema(title = "短信模板变量对应的实际值")
    private Map<String, String> templateParams;

    @Schema(title = "模板内容，长度为1~500个字符")
    private String content;
}
