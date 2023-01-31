package com.kabunx.component.openfeign;

import com.kabunx.component.common.constant.RequestConstants;
import com.kabunx.component.common.constant.SecurityConstants;
import com.kabunx.component.common.context.AuthContext;
import com.kabunx.component.common.context.AuthContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Objects;

/**
 * 全局配置,对内部请求添加用户信息
 */
public class AuthRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        AuthContext authContext = AuthContextHolder.getCurrentAuth();
        if (Objects.nonNull(authContext)) {
            template.header(RequestConstants.HEADER_AUTH_TYPE, authContext.getType());
            template.header(RequestConstants.HEADER_AUTH_ID, String.valueOf(authContext.getId()));
        }
    }
}
