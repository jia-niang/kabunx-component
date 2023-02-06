package com.kabunx.component.web.servlet;

import com.kabunx.component.common.dto.DTO;
import com.kabunx.component.common.util.JsonUtils;
import com.kabunx.component.web.util.ServletRequestUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 请求日志拦截器
 */
@Slf4j
public class LogHandlerInterceptor extends HandlerInterceptorAdapter {

    private final String START_TIME = "startTime";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        // 设置请求开始时间
        request.setAttribute(START_TIME, startTime);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, @Nullable Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        long startTime = (Long) request.getAttribute(START_TIME);
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        RequestLog requestLog = new RequestLog();
        requestLog.setExecuteTime(executeTime);
        requestLog.setIp(ServletRequestUtils.getClientIp(request));
        requestLog.setMethod(request.getMethod());
        requestLog.setUri(request.getRequestURI());
        requestLog.setQueries(request.getParameterMap());
        log.info("[RequestLog] 请求信息 = {}", requestLog);
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class RequestLog extends DTO {

        private Long executeTime;
        /**
         * IP地址
         */
        private String ip;

        /**
         * 请求类型
         */
        private String method;

        /**
         * URL
         */
        private String uri;

        /**
         * 请求参数
         */
        private Map<String, String[]> queries;

        private byte[] body;

        @Override
        public String toString() {
            return JsonUtils.object2Json(this);
        }
    }
}
