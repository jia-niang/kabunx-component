package com.kabunx.component.jwt;

import lombok.Data;

import java.io.Serializable;

@Data
public class JwtPair implements Serializable {
    private String accessToken;
    private String refreshToken;
}
