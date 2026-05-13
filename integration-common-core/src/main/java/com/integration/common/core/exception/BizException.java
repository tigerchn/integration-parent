package com.integration.common.core.exception;

import com.integration.common.core.api.ResultCode;

/**
 * 携带 {@link ResultCode} 的业务异常，供全局异常处理转换为统一响应。
 */
public class BizException extends RuntimeException {

    private final ResultCode resultCode;

    /**
     * 使用结果码默认消息。
     *
     * @param resultCode 业务结果码
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /**
     * 指定自定义消息。
     *
     * @param resultCode 业务结果码
     * @param message    异常消息
     */
    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 指定原因链。
     *
     * @param resultCode 业务结果码
     * @param message    异常消息
     * @param cause      底层原因
     */
    public BizException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    /** @return 关联的业务结果码 */
    public ResultCode getResultCode() {
        return resultCode;
    }
}
