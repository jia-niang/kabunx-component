package com.kabunx.component.jwt;

import com.kabunx.component.common.context.AuthContext;
import com.kabunx.component.common.util.JsonUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JWT处理的业务类
 * 主要做两件事：生成jwt，解析jwt
 */
@Slf4j
public class JwtGenerator {
    private static final String MAC_SECRET = "1234567890QwertyuiopAsdfghklZxcvbnm";

    private final JwtProperties jwtProperties;

    private final JWSHeader jwsHeader;

    public JwtGenerator(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        // 创建JWS头，设置签名算法和类型
        this.jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
    }

    public String generateByHMAC(AuthContext authContext) {
        if (Objects.isNull(authContext)) {
            log.error("签名信息不能为Null");
        }
        JwtPayload jwtPayload = new JwtPayload();
        jwtPayload.setSub(jwtProperties.getSub());
        jwtPayload.setIss(jwtProperties.getIss());
        jwtPayload.setAud(String.valueOf(authContext.getId()));
        jwtPayload.setType(authContext.getType());
        jwtPayload.setUsername(authContext.getUsername());
        jwtPayload.setAuthorities(authContext.getAuthorities());
        String body = jwtPayload.toPayloadString(jwtProperties.getAccessExpDays());
        try {
            //创建JWS对象
            JWSObject jwsObject = new JWSObject(jwsHeader, new Payload(body));
            //创建HMAC签名器
            JWSSigner jwsSigner = new MACSigner(MAC_SECRET);
            //签名
            jwsObject.sign(jwsSigner);
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("JWT生成错误", e);
            return null;
        }
    }


    public JwtPayload verifyByHMAC(String token) {
        try {
            // 从token中解析JWS对象
            JWSObject jwsObject = JWSObject.parse(token);
            // 创建HMAC验证器
            JWSVerifier jwsVerifier = new MACVerifier(MAC_SECRET);
            if (!jwsObject.verify(jwsVerifier)) {
                log.error("JWT签名不合法！");
                return null;
            }
            String payload = jwsObject.getPayload().toString();
            JwtPayload jwtPayload = JsonUtils.json2Object(payload, JwtPayload.class);
            return isExpired(jwtPayload.getExp()) ? null : jwtPayload;
        } catch (ParseException | JOSEException e) {
            log.error("JWT解析异常", e);
        }
        return null;
    }

    public boolean isExpired(LocalDateTime exp) {
        return LocalDateTime.now().isAfter(exp);
    }
}
