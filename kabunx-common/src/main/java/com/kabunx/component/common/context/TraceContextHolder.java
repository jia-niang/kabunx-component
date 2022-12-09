package com.kabunx.component.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.io.Serializable;

public class TraceContextHolder implements Serializable {
    private static final ThreadLocal<TraceContext> TRACE_CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public TraceContextHolder() {
    }

    public static void setTrace(TraceContext traceContext) {
        TRACE_CONTEXT_THREAD_LOCAL.set(traceContext);
    }

    public static TraceContext getTrace() {
        return TRACE_CONTEXT_THREAD_LOCAL.get();
    }

    public static void removeTrace() {
        TRACE_CONTEXT_THREAD_LOCAL.remove();
    }
}
