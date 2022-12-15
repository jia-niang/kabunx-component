package com.kabunx.component.sms.aliyun;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AliyunResponseData implements Serializable {
    private static final String OK = "OK";

    @JsonProperty("Message")
    private String message;

    @JsonProperty("BizId")
    private String bizId;

    @JsonProperty("Code")
    private String code;

    public boolean success() {
        return OK.equals(code);
    }
}
