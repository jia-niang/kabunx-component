package com.kabunx.component.elasticsearch.util;

import com.kabunx.component.elasticsearch.annotation.Document;

public class ReflectionUtils {

    public static String getIndexName(Class<?> clazz) {
        if (clazz.getAnnotation(Document.class) != null) {
            return clazz.getAnnotation(Document.class).indexName();
        }
        return null;
    }

    public static String getIndexName(Object entity) {
        return getIndexName(entity.getClass());
    }
}
