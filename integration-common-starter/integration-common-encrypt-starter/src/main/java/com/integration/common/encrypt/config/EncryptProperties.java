package com.integration.common.encrypt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.encrypt")
public class EncryptProperties {

    /**
     * 全局开关（默认关闭；与 {@code integration.encrypt.enable} 自动装配条件一致，需在配置中显式开启）。
     */
    private boolean enable = false;

    /**
     * RSA 公钥（Base64 DER）
     */
    private String rsaPublicKey;

    /**
     * RSA 私钥（Base64 DER）
     */
    private String rsaPrivateKey;

    /**
     * JDK Cipher 算法名，需与客户端一致。默认 OAEP(SHA-256)。
     */
    private String rsaTransformation = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * AES 密钥长度
     */
    private int aesKeySize = 256;

    /**
     * 跳过解密过滤器处理的路径：不含通配符时按 URI 前缀匹配；含 * 或 ? 时按 Ant 模式匹配。
     */
    private String[] excludePaths = {};

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getRsaPublicKey() {
        return rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    public void setRsaPrivateKey(String rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public String getRsaTransformation() {
        return rsaTransformation;
    }

    public void setRsaTransformation(String rsaTransformation) {
        this.rsaTransformation = rsaTransformation;
    }

    public int getAesKeySize() {
        return aesKeySize;
    }

    public void setAesKeySize(int aesKeySize) {
        this.aesKeySize = aesKeySize;
    }

    public String[] getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = excludePaths;
    }
}
