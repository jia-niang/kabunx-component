package com.kabunx.component.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "通用分页数据")
public class Pagination<T> extends DTO {

    @Schema(title = "当前页", required = true)
    private long current;
    @Schema(title = "总数", required = true)
    private long total;
    @Schema(title = "当前页数据集", required = true)
    private List<T> list;

    public Pagination() {
    }

    public Pagination(long current, long total, List<T> list) {
        this.current = current;
        this.total = total;
        this.list = list;
    }
}
