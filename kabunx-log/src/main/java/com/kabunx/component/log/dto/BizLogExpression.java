package com.kabunx.component.log.dto;

import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 业务日志表达式字段
 */
@Data
public class BizLogExpression {
    private final String content;
    private final String operator;

    private final String bizNo;

    private final String type;
    private final String subType;
    private final Integer count;
    private final String extra;
    private final String condition;

    private final boolean sync;

    public BizLogExpression(Builder builder) {
        this.content = builder.getContent();
        this.operator = builder.getOperator();
        this.bizNo = builder.getBizNo();
        this.type = builder.getType();
        this.subType = builder.getSubType();
        this.count = builder.getCount();
        this.extra = builder.getExtra();
        this.condition = builder.getCondition();
        this.sync = builder.isSync();
    }


    @Data
    public static class Builder {
        private String content = "";

        private String operator = "";

        private String type = "";
        private String subType = "";
        private String bizNo = "";

        @NonNull
        private Integer count = 1;

        @Nullable
        private String extra;
        private String condition = "";

        private boolean sync;
    }
}
