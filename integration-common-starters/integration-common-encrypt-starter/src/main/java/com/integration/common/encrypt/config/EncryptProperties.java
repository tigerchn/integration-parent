package com.integration.common.encrypt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.encrypt")
public class EncryptProperties {

    /**
     * 总开关：为 {@code true} 时才装配本 starter（与 {@code integration.encrypt.enable} 一致）。
     * 在此基础上，由 {@link #isDecryptRequestBodyEnabled()}、{@link #isEncryptResponseBodyEnabled()} 分别控制请求解密与响应加密。
     */
    private boolean enable = false;

    /**
     * 请求体解密开关（YAML：{@code decrypt-request-body-enabled}）。
     * <ul>
     *   <li>{@code true}：注册解密过滤器，对符合条件的 JSON 请求按 key/data 解密</li>
     *   <li>{@code false}（默认）：不注册解密过滤器，不做请求解密</li>
     * </ul>
     */
    private boolean decryptRequestBodyEnabled = false;

    /**
     * 请求解密路径白名单（仅在 {@link #isDecryptRequestBodyEnabled()} 为 true 时生效）：
     * 无有效项（未配置或均为空串）时表示<strong>全路径</strong>；配置了非空规则时仅解密匹配路径。
     */
    private String[] decryptRequestPathPatterns = {};

    /**
     * 响应体加密开关（YAML：{@code encrypt-response-body-enabled}）。
     * <ul>
     *   <li>{@code true}：注册响应体 {@code ControllerAdvice}，对符合条件的响应加密为 key/data</li>
     *   <li>{@code false}（默认）：不注册响应加密，不做响应加密</li>
     * </ul>
     */
    private boolean encryptResponseBodyEnabled = false;

    /**
     * 响应加密路径白名单（仅在 {@link #isEncryptResponseBodyEnabled()} 为 true 时生效）：
     * 无有效项时表示<strong>全路径</strong>；配置了非空规则时仅加密匹配路径。
     */
    private String[] encryptResponsePathPatterns = {};

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
     * 请求体解密与响应体加密均跳过的路径：不含通配符时按 URI 前缀匹配；含 * 或 ? 时按 Ant 模式匹配。
     */
    private String[] excludePaths = {};

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDecryptRequestBodyEnabled() {
        return decryptRequestBodyEnabled;
    }

    public void setDecryptRequestBodyEnabled(boolean decryptRequestBodyEnabled) {
        this.decryptRequestBodyEnabled = decryptRequestBodyEnabled;
    }

    public String[] getDecryptRequestPathPatterns() {
        return decryptRequestPathPatterns;
    }

    public void setDecryptRequestPathPatterns(String[] decryptRequestPathPatterns) {
        this.decryptRequestPathPatterns = decryptRequestPathPatterns;
    }

    public boolean isEncryptResponseBodyEnabled() {
        return encryptResponseBodyEnabled;
    }

    public void setEncryptResponseBodyEnabled(boolean encryptResponseBodyEnabled) {
        this.encryptResponseBodyEnabled = encryptResponseBodyEnabled;
    }

    public String[] getEncryptResponsePathPatterns() {
        return encryptResponsePathPatterns;
    }

    public void setEncryptResponsePathPatterns(String[] encryptResponsePathPatterns) {
        this.encryptResponsePathPatterns = encryptResponsePathPatterns;
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
