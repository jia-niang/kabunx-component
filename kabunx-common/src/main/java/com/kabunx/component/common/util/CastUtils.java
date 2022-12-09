package com.kabunx.component.common.util;

public class CastUtils {
    public static <T> T cast(Object data, Class<T> tClass) {
        return tClass.cast(data);
    }
}
