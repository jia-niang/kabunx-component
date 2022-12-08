package com.kabunx.component.jwt;

import com.kabunx.component.core.util.JsonUtils;
import lombok.Data;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class JwtPayload {
    /**
     * 签发者
     **/
    private String iss;
    /**
     * 签发的用户
     **/
    private String sub;
    /**
     * 接收方
     **/
    private String aud;
    /**
     * 过期时间，这个过期时间必须要大于签发时间
     **/
    private LocalDateTime exp;
    /**
     * 签发时间
     **/
    private final LocalDateTime iat = LocalDateTime.now();
    /**
     * 唯一身份标识，主要用来作为一次性token,从而回避重放攻击
     **/
    private final String jti = UUID.randomUUID().toString();

    /**
     * 用户类型
     */
    private String type;

    /**
     * 用户名
     */
    private String username;
    /**
     * 权限集
     */
    private Set<String> authorities = new HashSet<>();

    public JwtPayload() {
    }

    public String toPayloadString(int days) {
        Assert.isTrue(days > 0, "jwt expireDate must after now");
        exp = iat.plusDays(days);

        return JsonUtils.object2Json(this);
    }
}
