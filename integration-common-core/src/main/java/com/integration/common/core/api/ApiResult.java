package com.integration.common.core.api;

import java.time.Instant;

/**
 * 统一 API 响应体，包含成功标识、业务码、消息、载荷、链路 ID 与时间戳。
 *
 * @param <T> 业务数据类型
 */
public final class ApiResult<T> {

    private boolean success;
    private int code;
    private String message;
    private T data;
    private String traceId;
    private Instant timestamp;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 构造成功响应并携带数据。
     *
     * @param data 业务数据，可为 {@code null}
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ApiResult<T> ok(T data) {
        ApiResult<T> r = new ApiResult<>();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        r.setData(data);
        r.setTimestamp(Instant.now());
        return r;
    }

    /**
     * 构造无载荷的成功响应。
     *
     * @return 成功响应
     */
    public static ApiResult<Void> ok() {
        return ok(null);
    }

    /**
     * 按 {@link ResultCode} 构造失败响应，可覆盖默认消息。
     *
     * @param code    业务结果码
     * @param message 提示文案，为 {@code null} 时使用 {@code code} 的默认消息
     * @param <T>     数据类型占位
     * @return 失败响应
     */
    public static <T> ApiResult<T> fail(ResultCode code, String message) {
        ApiResult<T> r = new ApiResult<>();
        r.setSuccess(false);
        r.setCode(code.getCode());
        r.setMessage(message != null ? message : code.getMessage());
        r.setTimestamp(Instant.now());
        return r;
    }

    /**
     * 使用数值业务码构造失败响应。
     *
     * @param code    业务码
     * @param message 提示文案
     * @param <T>     数据类型占位
     * @return 失败响应
     */
    public static <T> ApiResult<T> fail(int code, String message) {
        ApiResult<T> r = new ApiResult<>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMessage(message);
        r.setTimestamp(Instant.now());
        return r;
    }
}
