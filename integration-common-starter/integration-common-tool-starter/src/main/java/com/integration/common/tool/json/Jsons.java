package com.integration.common.tool.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * 基于 Spring 容器中的 {@link ObjectMapper} 提供 JSON 读写封装。
 */
public final class Jsons {

    private final ObjectMapper mapper;

    /**
     * @param mapper 用于序列化/反序列化的 {@link ObjectMapper}
     */
    public Jsons(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param value 任意可序列化对象
     * @return JSON 文本
     * @throws IllegalStateException 序列化失败时包装抛出
     */
    public String write(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize JSON", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为指定类型。
     *
     * @param json JSON 文本
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 反序列化结果
     */
    public <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** @return 底层 {@link ObjectMapper} */
    public ObjectMapper mapper() {
        return mapper;
    }
}
