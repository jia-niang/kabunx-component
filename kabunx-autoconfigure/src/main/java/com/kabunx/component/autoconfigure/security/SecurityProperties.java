package com.kabunx.component.autoconfigure.security;

import com.kabunx.component.jwt.JwtProperties;
import com.kabunx.component.security.properties.PathProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SecurityProperties.PREFIX)
public class SecurityProperties {
    public static final String PREFIX = "security";

    JwtProperties jwt;

    PathProperties path;
}
