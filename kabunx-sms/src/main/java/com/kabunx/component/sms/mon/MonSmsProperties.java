package com.kabunx.component.sms.mon;

import lombok.Data;

import java.io.Serializable;

@Data
public class MonSmsProperties implements Serializable {
    private Boolean enabled;
    private String userId;
    private String pwd;
}
