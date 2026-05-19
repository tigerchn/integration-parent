package com.integration.common.core.exception;

import com.integration.common.core.api.ResultCode;

/**
 * 平台统一异常基类，携带 {@link ResultCode}，供 web 层全局异常处理映射为 {@link com.integration.common.core.api.ApiResult}。
 * <p>
 * 各 starter 领域异常应继承此类（或继承 {@link BizException}），避免 web-starter 依赖具体 starter。
 */
public abstract class IntegrationException extends RuntimeException {

    private final ResultCode resultCode;

    protected IntegrationException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    protected IntegrationException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    protected IntegrationException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
