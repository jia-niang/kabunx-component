package com.kabunx.component.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;

import java.io.Serializable;

public class TraceContextHolder implements Serializable {
    public static final String TRACE_ID = "traceId";
    private static final ThreadLocal<TraceContext> TRACE_CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public TraceContextHolder() {
    }

    public static void setTrace(TraceContext traceContext) {
        TRACE_CONTEXT_THREAD_LOCAL.set(traceContext);
        MDC.put(TRACE_ID, traceContext.getTraceId());
    }

    public static TraceContext getTrace() {
        return TRACE_CONTEXT_THREAD_LOCAL.get();
    }

    public static void removeTrace() {
        TRACE_CONTEXT_THREAD_LOCAL.remove();
        MDC.remove(TRACE_ID);
    }
}
