package com.kabunx.component.security.dto;

import com.kabunx.component.core.dto.DTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WechatRequest extends DTO {
    private String code;
    private String iv;
    private String data;
}
