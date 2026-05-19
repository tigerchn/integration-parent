package com.integration.client.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "integration.client.security")
public class ClientSecurityProperties {

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

        private String secret = "integration-client-jwt-secret-change-me-32b!!";
        private long expiresInSeconds = 604800L;

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
