package com.integration.common.encrypt.exception;

import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.IntegrationException;

/**
 * 请求/响应加解密失败。
 */
public class EncryptException extends IntegrationException {

    public EncryptException(String message) {
        super(ResultCode.BAD_REQUEST, message);
    }

    public EncryptException(String message, Throwable cause) {
        super(ResultCode.BAD_REQUEST, message, cause);
    }
}
