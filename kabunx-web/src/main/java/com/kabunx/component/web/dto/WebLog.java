package com.kabunx.component.web.dto;

import com.kabunx.component.core.dto.DTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WebLog extends DTO {
    /**
     * IP地址
     */
    private String ip;

    /**
     * 请求类型
     */
    private String method;

    /**
     * URL
     */
    private String uri;

    /**
     * 请求参数
     */
    private Object args;

    /**
     * 请求类和方法
     */
    private String signature;
}
