package com.kabunx.component.web.util;

import com.kabunx.component.common.constant.GlobalConstants;
import com.kabunx.component.common.constant.RequestConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Objects;

@Slf4j
public class ServletRequestUtils {
    private static final String[] HEADER_IP_KEYWORDS = {
            RequestConstants.HEADER_CLIENT_IP,
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "X-Real-IP"
    };

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";

    private static final String SEPARATOR = ",";

    /**
     * 获取客户IP地址
     *
     * @param request 客户端请求
     * @return 客户端IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = null;
        try {
            for (String header : HEADER_IP_KEYWORDS) {
                ipAddress = request.getHeader(header);
                if (!StringUtils.isEmpty(ipAddress) && !UNKNOWN.equalsIgnoreCase(ipAddress)) {
                    break;
                }
            }
            if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    // 根据网卡取本机配置的 IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    if (Objects.nonNull(inet)) {
                        ipAddress = inet.getHostAddress();
                    }
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            //  "***.***.***.***"
            if (Objects.nonNull(ipAddress)) {
                ipAddress = ipAddress.split(SEPARATOR)[0].trim();
            }
        } catch (Exception ex) {
            log.error("[Util] 获取客户端 IP 异常", ex);
            ipAddress = "";
        }
        return Objects.isNull(ipAddress) ? "" : ipAddress;
    }
}
