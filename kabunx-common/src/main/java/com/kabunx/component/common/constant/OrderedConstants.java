package com.kabunx.component.common.constant;

/**
 * 请注意越低的优先级越高
 */
public interface OrderedConstants {
    int DEFAULT = 0;

    int TRACE_HANDLER = -20;

    int AUTH_HANDLER = -19;
    int LOG_HANDLER = -18;

    int BIZ_LOG = -10;

    int MULTI_TENANT = -1;

    int DYNAMIC_DATASOURCE = 0;

    int L2CACHE = 2;
}
