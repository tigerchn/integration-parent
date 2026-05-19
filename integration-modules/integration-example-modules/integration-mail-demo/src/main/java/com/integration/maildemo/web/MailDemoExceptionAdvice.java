package com.integration.maildemo.web;

import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.trace.TraceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 将 SMTP 发送失败映射为可读业务响应（避免仅出现通用 500）。
 */
@RestControllerAdvice
public class MailDemoExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(MailDemoExceptionAdvice.class);

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ApiResult<Void>> handleMail(MailException ex) {
        log.warn("Mail send failed: {}", ex.getMessage());
        Throwable root = ex.getCause() != null ? ex.getCause() : ex;
        String detail = root.getMessage() != null ? root.getMessage() : ex.getMessage();
        ApiResult<Void> body = ApiResult.fail(ResultCode.INTERNAL_ERROR, "Mail delivery failed: " + detail);
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }
}
