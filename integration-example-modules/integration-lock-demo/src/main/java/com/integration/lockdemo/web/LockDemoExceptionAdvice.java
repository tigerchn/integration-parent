package com.integration.lockdemo.web;

import com.integration.common.core.api.ApiResult;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.trace.TraceConstants;
import com.integration.common.redisson.lock.DistributedLockAcquireException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 将抢锁失败映射为 409，便于 Postman 区分「业务成功」与「锁竞争失败」（不经由通用 500 兜底）。
 */
@RestControllerAdvice
public class LockDemoExceptionAdvice {

    @ExceptionHandler(DistributedLockAcquireException.class)
    public ResponseEntity<ApiResult<Void>> handleLockAcquire(DistributedLockAcquireException ex) {
        ApiResult<Void> body = ApiResult.fail(ResultCode.CONFLICT, ex.getMessage());
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
