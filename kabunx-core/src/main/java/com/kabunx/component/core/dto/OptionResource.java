package com.kabunx.component.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "选项资源（包含子集）")
public class OptionResource<T> extends Resource {

    @Schema(title = "唯一值")
    private T value;
    @Schema(title = "展示标签")
    private String label;
    @Schema(title = "子集")
    private List<OptionResource<T>> children;
}
