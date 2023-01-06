package com.kabunx.component.openfeign;

import com.kabunx.component.common.constant.SecurityConstants;
import com.kabunx.component.common.context.TraceContext;
import com.kabunx.component.common.context.TraceContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Objects;

/**
 * 全局配置,内部链路数据
 */
public class TraceRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        TraceContext traceContext = TraceContextHolder.getTrace();
        if (Objects.nonNull(traceContext)) {
            template.header(SecurityConstants.HEADER_CLIENT_ID, traceContext.getClientId());
            template.header(SecurityConstants.HEADER_TRACE_ID, traceContext.getTraceId());
        }
    }
}
