package com.integration.admin.web.exception;

import com.integration.admin.security.AdminSecurityErrorHints;
import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.trace.TraceConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 管理端 API 专用异常处理：补充 Spring Security 在 Web 层抛出的权限异常，避免被通用 {@code Exception} 处理器误转为 500。
 */
@RestControllerAdvice
@Order(1)
public class AdminApiExceptionHandler {

    /**
     * 处理方法级安全（如 {@code @PreAuthorize}）等场景下的访问拒绝。
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<Void>> handleAccessDenied(AccessDeniedException ex, ServletWebRequest webRequest) {
        HttpServletRequest request = webRequest.getRequest();
        String uri = request != null ? request.getRequestURI() : "";
        String message = AdminSecurityErrorHints.forbidden(uri, ex);
        ApiResult<Void> body = ApiResult.fail(ResultCode.FORBIDDEN, message);
        attachTrace(body);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    private static void attachTrace(ApiResult<?> body) {
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
    }
}
