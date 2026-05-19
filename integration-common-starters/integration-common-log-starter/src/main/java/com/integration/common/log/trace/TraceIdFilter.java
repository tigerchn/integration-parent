package com.integration.common.log.trace;

import com.integration.common.core.trace.TraceConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 为每个 HTTP 请求生成或透传 traceId，写入响应头与 MDC，便于日志关联。
 */
public class TraceIdFilter extends OncePerRequestFilter {

    /**
     * 从请求头读取 traceId，缺失则生成；在 finally 中清理 MDC。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = request.getHeader(TraceConstants.HEADER_TRACE_ID);
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        response.setHeader(TraceConstants.HEADER_TRACE_ID, traceId);
        MDC.put(TraceConstants.TRACE_ID, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TraceConstants.TRACE_ID);
        }
    }
}
