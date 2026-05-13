package com.integration.common.core.api;

/**
 * 与 HTTP/业务场景对应的统一结果码枚举。
 */
public enum ResultCode {

    SUCCESS(0, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    INTERNAL_ERROR(500, "Internal Server Error"),

    BUSINESS_ERROR(40001, "Business rule violation"),
    VALIDATION_ERROR(40002, "Validation failed");

    private final int code;
    private final String message;

    /**
     * @param code    数值码
     * @param message 默认描述
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /** @return 数值业务码 */
    public int getCode() {
        return code;
    }

    /** @return 默认描述文案 */
    public String getMessage() {
        return message;
    }
}
