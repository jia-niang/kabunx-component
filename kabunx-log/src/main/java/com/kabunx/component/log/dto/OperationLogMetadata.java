package com.kabunx.component.log.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationLogMetadata {
    private String successTemplate;
    private String failTemplate;
    private String operator;
    private String type;
    private String subType;
    private String bizNo;
    private Integer count;
    private String extra;
    private String condition;
    private String success;
}
