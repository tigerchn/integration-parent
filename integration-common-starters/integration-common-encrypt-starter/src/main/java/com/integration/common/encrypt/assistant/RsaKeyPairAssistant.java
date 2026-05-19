package com.integration.common.encrypt.assistant;

import com.integration.common.encrypt.util.RsaUtil;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 生成 {@code integration.encrypt.rsa-public-key} / {@code rsa-private-key} 所需的 Base64 字符串，
 * 编码格式与 {@link RsaUtil} 一致：公钥为 X.509 SubjectPublicKeyInfo，私钥为 PKCS#8。
 */
public final class RsaKeyPairAssistant {

    public static final int DEFAULT_KEY_SIZE_BITS = 2048;

    private RsaKeyPairAssistant() {
    }

    /**
     * Base64(DER) 形式的 RSA 密钥对，可直接写入 {@code application.yml}。
     *
     * @param rsaPublicKey  X.509 编码公钥
     * @param rsaPrivateKey PKCS#8 编码私钥
     */
    public record KeyPairBase64(String rsaPublicKey, String rsaPrivateKey) {
    }

    /**
     * 使用 {@link #DEFAULT_KEY_SIZE_BITS} 位生成密钥对。
     */
    public static KeyPairBase64 generateKeyPairBase64() {
        return generateKeyPairBase64(DEFAULT_KEY_SIZE_BITS);
    }

    /**
     * @param keySizeBits 常用 2048 或 3072
     */
    public static KeyPairBase64 generateKeyPairBase64(int keySizeBits) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(keySizeBits);
            KeyPair keyPair = generator.generateKeyPair();
            String pub = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String pri = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            return new KeyPairBase64(pub, pri);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM 不支持 RSA 密钥生成", e);
        }
    }
}
