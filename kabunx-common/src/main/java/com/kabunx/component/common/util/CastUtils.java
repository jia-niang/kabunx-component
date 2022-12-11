package com.kabunx.component.common.util;

import java.util.List;

/**
 * 通用数据转化工具类
 */
public class CastUtils {
    public static <T> T cast(Object data, Class<T> tClass) {
        return tClass.cast(data);
    }

    public static <T> List<T> toArray(Class<T> tClass) {
        return null;
    }
}
