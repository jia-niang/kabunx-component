package com.kabunx.component.security.dto;

import com.kabunx.component.core.dto.DTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CaptchaRequest extends DTO {
    private String phone;
    private String captcha;
}
