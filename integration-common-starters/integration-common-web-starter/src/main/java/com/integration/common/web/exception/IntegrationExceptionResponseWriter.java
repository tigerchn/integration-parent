package com.integration.common.web.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.IntegrationException;
import com.integration.common.core.trace.TraceConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将 {@link IntegrationException} 写成统一 {@link ApiResult} JSON。
 * <p>
 * 供 {@link GlobalExceptionHandler} 与 Servlet Filter 等 MVC 之外的入口共用，避免响应格式不一致。
 */
public final class IntegrationExceptionResponseWriter {

    private IntegrationExceptionResponseWriter() {
    }

    public static ApiResult<Void> toBody(IntegrationException ex) {
        ApiResult<Void> body = ApiResult.fail(ex.getResultCode(), ex.getMessage());
        attachTrace(body);
        return body;
    }

    public static HttpStatus resolveStatus(IntegrationException ex) {
        return ResultCodeHttpStatus.resolve(ex.getResultCode());
    }

    public static void write(HttpServletResponse response, ObjectMapper objectMapper, IntegrationException ex)
            throws IOException {
        ApiResult<Void> body = toBody(ex);
        HttpStatus status = resolveStatus(ex);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private static void attachTrace(ApiResult<?> body) {
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
    }
}
