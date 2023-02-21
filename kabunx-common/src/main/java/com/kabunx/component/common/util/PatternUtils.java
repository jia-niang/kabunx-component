package com.kabunx.component.common.util;

import org.springframework.lang.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用正则工具类
 */
public class PatternUtils {
    private static final Pattern urlPattern = Pattern.compile("(\\w+\\.)?([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]\\.)+[a-zA-Z]{2,}");

    private static final String phoneRegex = "^1[3-9]\\d{9}$";

    @Nullable
    public static String getSubdomain(String url) {
        Matcher matcher = urlPattern.matcher(url);
        if (matcher.find()) {
            String domain = matcher.group();
            String[] parts = domain.split("\\.");
            if (parts.length > 2) {
                String subdomain = parts[0];
                System.out.println(subdomain);
            }
        }
        return null;
    }

    public static boolean isPhoneNumber(String number) {
        return Pattern.matches(phoneRegex, number);
    }
}
