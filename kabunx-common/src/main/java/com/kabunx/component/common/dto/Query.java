package com.kabunx.component.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Query request from Client.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Query extends DTO {
    private final int max = 200;
}
