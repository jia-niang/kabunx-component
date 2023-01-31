package com.kabunx.component.web.servlet;


import com.kabunx.component.common.constant.RequestConstants;
import com.kabunx.component.common.context.TraceContext;
import com.kabunx.component.common.context.TraceContextHolder;
import com.kabunx.component.common.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 提取头信息中的客户端追踪的信息，并将信息保存在上下文中
 */
@Slf4j
public class TraceHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        String clientId = request.getHeader(RequestConstants.HEADER_CLIENT_ID);
        String traceId = request.getHeader(RequestConstants.HEADER_TRACE_ID);
        if (!StringUtils.hasText(traceId)) {
            traceId = RandomUtils.uuid().toLowerCase();
        }
        // 追踪上下文
        TraceContext traceContext = new TraceContext();
        traceContext.setClientId(clientId);
        traceContext.setTraceId(traceId);
        TraceContextHolder.setTrace(traceContext);
        // 添加自定义头信息（只能放这里）
        response.addHeader(RequestConstants.HEADER_CLIENT_ID, traceContext.getClientId());
        response.addHeader(RequestConstants.HEADER_TRACE_ID, traceContext.getTraceId());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) throws Exception {
        TraceContextHolder.removeTrace();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
