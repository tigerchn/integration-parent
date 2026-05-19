package com.integration.admin.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 管理端安全配置，前缀 {@code integration.admin.security}。
 */
@ConfigurationProperties(prefix = "integration.admin.security")
public class AdminSecurityProperties {

    /**
     * 为 {@code true} 时，登出后的 JWT（按 jti）在 Redis 中保留至过期。
     */
    private boolean redisTokenBlacklistEnabled = true;

    @NestedConfigurationProperty
    private Jwt jwt = new Jwt();

    public boolean isRedisTokenBlacklistEnabled() {
        return redisTokenBlacklistEnabled;
    }

    public void setRedisTokenBlacklistEnabled(boolean redisTokenBlacklistEnabled) {
        this.redisTokenBlacklistEnabled = redisTokenBlacklistEnabled;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public static class Jwt {

        /**
         * HMAC-SHA256 密钥原始字符串，长度须不少于 256 bit（32 字节）。
         */
        private String secret = "integration-admin-jwt-secret-change-me-32b!";

        /** 访问令牌有效期（秒） */
        private long expiresInSeconds = 28800L;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpiresInSeconds() {
            return expiresInSeconds;
        }

        public void setExpiresInSeconds(long expiresInSeconds) {
            this.expiresInSeconds = expiresInSeconds;
        }
    }
}
