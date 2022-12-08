package com.kabunx.component.core.util;

public class CastUtils {
    public static <T> T cast(Object data, Class<T> tClass) {
        return tClass.cast(data);
    }
}
