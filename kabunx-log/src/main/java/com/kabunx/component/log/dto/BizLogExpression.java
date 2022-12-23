package com.kabunx.component.log.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 业务日志表达式字段
 */
@Data
@Builder
public class BizLogExpression {
    private String success;
    private String error;
    private String operator;
    private String type;
    private String subType;
    private String bizNo;
    private Integer count;
    private String extra;
    private String condition;
}
