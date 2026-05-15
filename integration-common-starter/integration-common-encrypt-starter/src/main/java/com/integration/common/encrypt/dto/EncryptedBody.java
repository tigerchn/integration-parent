package com.integration.common.encrypt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 客户端传入的密文请求体：RSA 加密的 AES 密钥 + AES-GCM 业务密文。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EncryptedBody(String key, String data) {
}
