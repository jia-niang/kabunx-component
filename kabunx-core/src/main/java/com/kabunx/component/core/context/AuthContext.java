package com.kabunx.component.core.context;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Schema(title = "已登录用户信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthContext implements Serializable {
    @Schema(title = "ID", required = true)
    private Long id;
    @Schema(title = "用户名", required = true)
    private String username;
    @Schema(title = "类型", required = true)
    private String type;
    @Schema(title = "角色", required = true)
    private Set<String> authorities;
    @Schema(title = "是否可用", required = true)
    private Boolean enabled;
}
