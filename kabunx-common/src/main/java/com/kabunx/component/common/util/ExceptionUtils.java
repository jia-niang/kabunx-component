package com.kabunx.component.common.util;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class ExceptionUtils {

    /**
     * 这里判断异常类型是否为CompletionException、ExecutionException，如果是则进行提取，否则直接返回。
     *
     * @param throwable this throwable
     * @return throwable
     */
    public static Throwable extractRealException(Throwable throwable) {
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }
}
