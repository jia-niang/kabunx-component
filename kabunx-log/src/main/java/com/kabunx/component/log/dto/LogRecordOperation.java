package com.kabunx.component.log.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogRecordOperation {
    private String successTemplate;
    private String failTemplate;
    private String operator;
    private String type;
    private String subType;
    private String bizNo;
    private String extra;
    private String condition;
    private String success;
}
