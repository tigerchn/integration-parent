package com.integration.common.web.exception;

import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.BizException;
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
 * 全局异常处理：将常见 Web/校验/业务异常转为统一 {@link ApiResult} 响应，并附带 traceId。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理 {@link BizException}。
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResult<Void>> handleBiz(BizException ex) {
        ApiResult<Void> body = ApiResult.fail(ex.getResultCode(), ex.getMessage());
        attachTrace(body);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 处理 Bean 校验失败（请求体绑定、表单绑定等）。
     */
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

    /**
     * 处理方法级约束校验异常。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraint(ConstraintViolationException ex) {
        ApiResult<Void> body = ApiResult.fail(ResultCode.VALIDATION_ERROR, ex.getMessage());
        attachTrace(body);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 处理请求格式错误、缺少参数、类型不匹配、非法参数等 400 场景。
     */
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

    /**
     * 处理静态资源未找到（如错误路径访问前端资源）。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResult<Void>> handleNoResource(NoResourceFoundException ex) {
        String path = ex.getResourcePath();
        String message = path != null ? "No static resource: " + path : ResultCode.NOT_FOUND.getMessage();
        ApiResult<Void> body = ApiResult.fail(ResultCode.NOT_FOUND, message);
        attachTrace(body);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * 兜底处理未分类异常，记录错误日志并返回 500。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ApiResult<Void> body = ApiResult.fail(ResultCode.INTERNAL_ERROR, ResultCode.INTERNAL_ERROR.getMessage());
        attachTrace(body);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * 从 MDC 读取 traceId 写入响应体。
     *
     * @param body 响应体
     */
    private static void attachTrace(ApiResult<?> body) {
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
    }
}
