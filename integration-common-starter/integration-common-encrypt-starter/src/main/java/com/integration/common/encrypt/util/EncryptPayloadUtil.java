package com.integration.common.encrypt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.encrypt.dto.EncryptedRequestBody;
import com.integration.common.encrypt.exception.EncryptException;
import org.springframework.util.StringUtils;

/**
 * 与 {@code @ApiDecrypt} / 过滤器约定一致：明文业务对象 ↔ {@code {"key":"...","data":"..."}} 密文 JSON。
 */
public final class EncryptPayloadUtil {

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    private EncryptPayloadUtil() {
    }

    /**
     * 将业务对象序列化为 JSON 后，按 RSA（封装 AES 密钥）+ AES-GCM 加密，得到可作为 {@code @ApiDecrypt} 接口请求体的 JSON 字符串。
     *
     * @param payload            明文请求体（由 Jackson 序列化）
     * @param rsaPublicKeyBase64 RSA 公钥 Base64（X.509）
     * @param rsaTransformation  {@link javax.crypto.Cipher} 算法名，须与服务端 {@code integration.encrypt.rsa-transformation} 一致
     * @param aesKeySizeBits     AES 密钥位数（128 / 192 / 256），须与服务端 {@code integration.encrypt.aes-key-size} 一致
     * @param objectMapper       用于序列化 {@code payload} 及外层 {@link EncryptedRequestBody}
     */
    public static <T> String encryptRequestPayload(T payload, String rsaPublicKeyBase64, String rsaTransformation,
                                                   int aesKeySizeBits, ObjectMapper objectMapper) {
        if (objectMapper == null) {
            throw new EncryptException("ObjectMapper 不能为 null");
        }
        if (!StringUtils.hasText(rsaPublicKeyBase64)) {
            throw new EncryptException("RSA 公钥不能为空");
        }
        try {
            EncryptedRequestBody body = toEncryptedRequestBody(payload, rsaPublicKeyBase64, rsaTransformation,
                    aesKeySizeBits, objectMapper);
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new EncryptException("序列化密文请求体失败", e);
        }
    }

    /**
     * 使用内置默认 {@link ObjectMapper}，行为与 {@link #encryptRequestPayload(Object, String, String, int, ObjectMapper)} 相同。
     */
    public static <T> String encryptRequestPayload(T payload, String rsaPublicKeyBase64, String rsaTransformation,
                                                   int aesKeySizeBits) {
        return encryptRequestPayload(payload, rsaPublicKeyBase64, rsaTransformation, aesKeySizeBits, DEFAULT_MAPPER);
    }

    /**
     * 解析密文 JSON，RSA 解密 AES 密钥、AES-GCM 解密业务数据后，反序列化为目标类型。
     *
     * @param encryptedRequestJson {@code {"key":"...","data":"..."}}
     * @param targetType           反序列化目标类型
     */
    public static <T> T decryptRequestPayload(String encryptedRequestJson, Class<T> targetType,
                                              String rsaPrivateKeyBase64, String rsaTransformation,
                                              ObjectMapper objectMapper) {
        return decryptRequestPayload(encryptedRequestJson,
                objectMapper == null ? null : objectMapper.constructType(targetType),
                rsaPrivateKeyBase64, rsaTransformation, objectMapper);
    }

    /**
     * 使用 {@link TypeReference} 支持带泛型的目标类型（如 {@code List<MyDto>}）。
     */
    public static <T> T decryptRequestPayload(String encryptedRequestJson, TypeReference<T> targetType,
                                              String rsaPrivateKeyBase64, String rsaTransformation,
                                              ObjectMapper objectMapper) {
        if (objectMapper == null) {
            throw new EncryptException("ObjectMapper 不能为 null");
        }
        return decryptRequestPayload(encryptedRequestJson, objectMapper.getTypeFactory().constructType(targetType),
                rsaPrivateKeyBase64, rsaTransformation, objectMapper);
    }

    /**
     * 使用内置默认 {@link ObjectMapper}，按 {@link Class} 反序列化。
     */
    public static <T> T decryptRequestPayload(String encryptedRequestJson, Class<T> targetType,
                                              String rsaPrivateKeyBase64, String rsaTransformation) {
        return decryptRequestPayload(encryptedRequestJson, targetType, rsaPrivateKeyBase64, rsaTransformation,
                DEFAULT_MAPPER);
    }

    /**
     * 使用内置默认 {@link ObjectMapper}，按 {@link TypeReference} 反序列化。
     */
    public static <T> T decryptRequestPayload(String encryptedRequestJson, TypeReference<T> targetType,
                                              String rsaPrivateKeyBase64, String rsaTransformation) {
        return decryptRequestPayload(encryptedRequestJson, targetType, rsaPrivateKeyBase64, rsaTransformation,
                DEFAULT_MAPPER);
    }

    private static <T> T decryptRequestPayload(String encryptedRequestJson, JavaType javaType,
                                               String rsaPrivateKeyBase64, String rsaTransformation,
                                               ObjectMapper objectMapper) {
        if (objectMapper == null) {
            throw new EncryptException("ObjectMapper 不能为 null");
        }
        if (javaType == null) {
            throw new EncryptException("反序列化目标类型不能为 null");
        }
        if (!StringUtils.hasText(rsaPrivateKeyBase64)) {
            throw new EncryptException("RSA 私钥不能为空");
        }
        if (!StringUtils.hasText(encryptedRequestJson)) {
            throw new EncryptException("密文请求体不能为空");
        }
        try {
            EncryptedRequestBody envelope = objectMapper.readValue(encryptedRequestJson, EncryptedRequestBody.class);
            if (envelope == null || !StringUtils.hasText(envelope.key()) || !StringUtils.hasText(envelope.data())) {
                throw new EncryptException("密文请求体须包含非空的 key、data 字段");
            }
            String aesKey = RsaUtil.decrypt(envelope.key(), rsaPrivateKeyBase64, rsaTransformation);
            String plainJson = AesGcmUtil.decrypt(envelope.data(), aesKey);
            return objectMapper.readValue(plainJson, javaType);
        } catch (JsonProcessingException e) {
            throw new EncryptException("解析密文请求体或明文 JSON 失败", e);
        }
    }

    /**
     * 生成 {@link EncryptedRequestBody}，便于自行序列化或调试。
     */
    public static <T> EncryptedRequestBody toEncryptedRequestBody(T payload, String rsaPublicKeyBase64,
                                                                  String rsaTransformation, int aesKeySizeBits,
                                                                  ObjectMapper objectMapper) {
        if (objectMapper == null) {
            throw new EncryptException("ObjectMapper 不能为 null");
        }
        if (!StringUtils.hasText(rsaPublicKeyBase64)) {
            throw new EncryptException("RSA 公钥不能为空");
        }
        try {
            String plainJson = objectMapper.writeValueAsString(payload);
            String aesKey = AesGcmUtil.generateKey(aesKeySizeBits);
            String data = AesGcmUtil.encrypt(plainJson, aesKey);
            String key = RsaUtil.encrypt(aesKey, rsaPublicKeyBase64, rsaTransformation);
            return new EncryptedRequestBody(key, data);
        } catch (JsonProcessingException e) {
            throw new EncryptException("序列化明文请求体失败", e);
        }
    }
}
