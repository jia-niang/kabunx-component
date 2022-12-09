package com.kabunx.component.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Page Query Param
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "分页查询参数")
public class PageQuery extends Query {

    public static final String ASC = "ASC";

    public static final String DESC = "DESC";

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Schema(title = "当前页", defaultValue = "1")
    private int page = 1;

    @Schema(title = "每页大小", defaultValue = "20")
    private int pageSize = DEFAULT_PAGE_SIZE;

    @Schema(title = "排序字段")
    private String orderBy;

    @Schema(title = "排序规则")
    private String orderDirection = DESC;

    @Schema(title = "分组")
    private String groupBy;

    /**
     * 从请求中构造分页信息
     *
     * @return 分页信息
     */
    public Page buildPage() {
        return new Page(page, pageSize);
    }

}
