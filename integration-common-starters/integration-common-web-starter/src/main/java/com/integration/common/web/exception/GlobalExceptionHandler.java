package com.integration.common.web.exception;

import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.IntegrationException;
import com.integration.common.core.trace.TraceConstants;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理：将常见 Web/校验/平台 {@link IntegrationException} 转为统一 {@link ApiResult}。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理各 starter / 业务抛出的 {@link IntegrationException}（含 {@link com.integration.common.core.exception.BizException}）。
     */
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ApiResult<Void>> handleIntegration(IntegrationException ex) {
        ApiResult<Void> body = IntegrationExceptionResponseWriter.toBody(ex);
        HttpStatus status = IntegrationExceptionResponseWriter.resolveStatus(ex);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResult<Void>> handleValidation(Exception ex) {
        String message;
        if (ex instanceof MethodArgumentNotValidException manv) {
            message = manv.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse(ResultCode.VALIDATION_ERROR.getMessage());
        } else if (ex instanceof BindException be) {
            message = be.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse(ResultCode.VALIDATION_ERROR.getMessage());
        } else {
            message = ResultCode.VALIDATION_ERROR.getMessage();
        }
        ApiResult<Void> body = ApiResult.fail(ResultCode.VALIDATION_ERROR, message);
        attachTrace(body);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraint(ConstraintViolationException ex) {
        ApiResult<Void> body = ApiResult.fail(ResultCode.VALIDATION_ERROR, ex.getMessage());
        attachTrace(body);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiResult<Void>> handleBadRequest(Exception ex) {
        ApiResult<Void> body = ApiResult.fail(ResultCode.BAD_REQUEST, ex.getMessage());
        attachTrace(body);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleNoResource(NoResourceFoundException ex) {
        String path = ex.getResourcePath();
        String message = path != null ? "No static resource: " + path : ResultCode.NOT_FOUND.getMessage();
        ApiResult<Void> body = ApiResult.fail(ResultCode.NOT_FOUND, message);
        attachTrace(body);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ApiResult<Void> body = ApiResult.fail(ResultCode.INTERNAL_ERROR, ResultCode.INTERNAL_ERROR.getMessage());
        attachTrace(body);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private static void attachTrace(ApiResult<?> body) {
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
    }
}
