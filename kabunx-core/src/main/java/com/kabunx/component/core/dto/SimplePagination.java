package com.kabunx.component.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "简单分页数据")
public class SimplePagination<T> extends DTO {
    @Schema(title = "是否还有更多", required = true)
    private Boolean hasMore;
    @Schema(title = "当前页数据集", required = true)
    private List<T> list;

    public SimplePagination() {
    }

    public SimplePagination(Boolean hasMore, List<T> list) {
        this.hasMore = hasMore;
        this.list = list;
    }
}
