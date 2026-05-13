package com.integration.common.core.trace;

/**
 * 分布式链路追踪相关常量（MDC 键与 HTTP 头）。
 */
public final class TraceConstants {

    /** MDC 中存放 traceId 的键名 */
    public static final String TRACE_ID = "traceId";
    /** 请求/响应中传递 traceId 的 HTTP 头名 */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";

    private TraceConstants() {
    }
}
