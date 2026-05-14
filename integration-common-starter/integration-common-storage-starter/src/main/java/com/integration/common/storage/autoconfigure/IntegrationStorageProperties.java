package com.integration.common.storage.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 对象存储统一配置，前缀 {@code integration.storage}。
 */
@ConfigurationProperties(prefix = "integration.storage")
public class IntegrationStorageProperties {

    /**
     * 为 {@code false} 时不注册 {@link com.integration.common.storage.StorageService}。
     */
    private boolean enabled = false;

    /**
     * {@code cos} 使用腾讯云 COS；{@code oss} 使用阿里云 OSS。
     */
    private StorageType type = StorageType.COS;

    @NestedConfigurationProperty
    private Cos cos = new Cos();

    @NestedConfigurationProperty
    private Oss oss = new Oss();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public StorageType getType() {
        return type;
    }

    public void setType(StorageType type) {
        this.type = type;
    }

    public Cos getCos() {
        return cos;
    }

    public void setCos(Cos cos) {
        if (cos != null) {
            this.cos = cos;
        }
    }

    public Oss getOss() {
        return oss;
    }

    public void setOss(Oss oss) {
        if (oss != null) {
            this.oss = oss;
        }
    }

    public enum StorageType {
        COS,
        OSS
    }

    /**
     * {@code integration.storage.cos.*}
     */
    public static class Cos {

        private String secretId;
        private String secretKey;
        /** 地域，如 {@code ap-guangzhou} */
        private String region;
        private String bucket;

        public String getSecretId() {
            return secretId;
        }

        public void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }

    /**
     * {@code integration.storage.oss.*}
     */
    public static class Oss {

        /** 如 {@code https://oss-cn-hangzhou.aliyuncs.com} */
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucket;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }
}
