package com.integration.encryptdemo.dto;

/**
 * 与 {@code POST /api/encrypt-demo/echo} 解密后的 JSON 结构一致。
 */
public record EchoRequest(String message) {
}
