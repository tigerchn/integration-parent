package com.integration.common.web.exception;

import com.integration.common.core.api.ResultCode;
import org.springframework.http.HttpStatus;

/**
 * 将 {@link ResultCode} 映射为 HTTP 状态码（仅用于 Web 层异常响应）。
 */
public final class ResultCodeHttpStatus {

    private ResultCodeHttpStatus() {
    }

    public static HttpStatus resolve(ResultCode resultCode) {
        return switch (resultCode) {
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case TOO_MANY_REQUESTS -> HttpStatus.TOO_MANY_REQUESTS;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case BAD_REQUEST, BUSINESS_ERROR, VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case SUCCESS -> HttpStatus.OK;
        };
    }
}
