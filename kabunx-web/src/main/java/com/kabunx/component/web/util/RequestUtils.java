package com.kabunx.component.web.util;

import com.kabunx.component.core.constant.GlobalConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class RequestUtils {
    private static final String USER_AGENT_FLAG = "user-agent";
    private static final String[] HEADER_IP_KEYWORDS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "X-Real-IP"
    };

    /***
     * 获取user-agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(USER_AGENT_FLAG);
    }

    /**
     * 获取客户IP地址
     *
     * @param request 客户端请求
     * @return 客户端IP
     */
    public static String getClientIp(HttpServletRequest request) {
        for (String header : HEADER_IP_KEYWORDS) {
            String ip = request.getHeader(header);
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                continue;
            }
            if (StringUtils.isNotEmpty(ip)) {
                return ip.split(GlobalConstants.SEPARATOR)[0];
            }
        }
        return request.getRemoteAddr();
    }
}
