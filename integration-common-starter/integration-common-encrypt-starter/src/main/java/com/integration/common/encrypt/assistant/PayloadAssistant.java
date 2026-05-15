package com.integration.common.encrypt.assistant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.dto.EncryptedBody;
import com.integration.common.encrypt.exception.EncryptException;
import com.integration.common.encrypt.util.AesUtil;
import com.integration.common.encrypt.util.RsaUtil;
import org.springframework.util.StringUtils;

public class PayloadAssistant {

    private final EncryptProperties properties;
    private final ObjectMapper objectMapper;

    public PayloadAssistant(EncryptProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public <T> String encrypt(T body) {
        String aesKey = AesUtil.generateKey(properties.getAesKeySize());
        String plainJson = body instanceof String s ? s : writeValueAsString(body);
        String data = AesUtil.encrypt(plainJson, aesKey);
        String key = RsaUtil.encrypt(aesKey, properties.getRsaPublicKey(), properties.getRsaTransformation());
        EncryptedBody encryptedBody = new EncryptedBody(key, data);
        return writeValueAsString(encryptedBody);
    }

    public <T> T decrypt(String body, Class<T> clazz) {
        EncryptedBody encrypted = readValue(body, EncryptedBody.class);
        if (encrypted == null || !StringUtils.hasText(encrypted.key()) || !StringUtils.hasText(encrypted.data())) {
            throw new EncryptException("入参解密失败：请求体须为 JSON 对象且包含非空的 key、data 字段");
        }

        String aesKey = RsaUtil.decrypt(encrypted.key(), properties.getRsaPrivateKey(), properties.getRsaTransformation());
        String realBody = AesUtil.decrypt(encrypted.data(), aesKey);
        return readValue(realBody, clazz);
    }


    private <T> T readValue(String body, Class<T> tClass) {
        if (tClass == null) {
            throw new EncryptException("需要转换的类型入参不能为空");
        }

        try {
            return objectMapper.readValue(body, tClass);
        } catch (JsonProcessingException e) {
            throw new EncryptException("Json 解析对象" + tClass.getName() + "异常");
        }
    }

    private <T> String writeValueAsString(T body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new EncryptException("序列化 Json 对象" + body.getClass().getName() + "异常");
        }
    }
}
