package com.integration.common.encrypt.exception;

public class EncryptException extends RuntimeException {
    public EncryptException(String msg) {
        super(msg);
    }

    public EncryptException(String msg, Throwable cause) {
        super(msg, cause);
    }
}