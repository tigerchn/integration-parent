package com.integration.common.core.exception;

import com.integration.common.core.api.ResultCode;

/**
 * 业务层异常，语义上表示可预期的业务失败；全局处理与 {@link IntegrationException} 相同。
 */
public class BizException extends IntegrationException {

    public BizException(ResultCode resultCode) {
        super(resultCode);
    }

    public BizException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public BizException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }
}
