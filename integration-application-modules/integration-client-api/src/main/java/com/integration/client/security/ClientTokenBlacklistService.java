package com.integration.client.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ClientTokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(ClientTokenBlacklistService.class);
    private static final String KEY_PREFIX = "client:security:jti:";

    private final ObjectProvider<StringRedisTemplate> redisTemplate;
    private final ClientSecurityProperties securityProperties;

    public ClientTokenBlacklistService(ObjectProvider<StringRedisTemplate> redisTemplate,
                                       ClientSecurityProperties securityProperties) {
        this.redisTemplate = redisTemplate;
        this.securityProperties = securityProperties;
    }

    public void add(String jti, Duration ttl) {
        StringRedisTemplate redis = redisTemplate.getIfAvailable();
        if (redis == null
                || !securityProperties.isRedisTokenBlacklistEnabled()
                || jti == null
                || ttl.isNegative()
                || ttl.isZero()) {
            return;
        }
        try {
            redis.opsForValue().set(KEY_PREFIX + jti, "1", ttl);
        } catch (DataAccessException ex) {
            log.warn("Redis token blacklist add skipped: {}", ex.getMessage());
        }
    }

    public boolean isBlocked(String jti) {
        StringRedisTemplate redis = redisTemplate.getIfAvailable();
        if (redis == null || !securityProperties.isRedisTokenBlacklistEnabled() || jti == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(redis.hasKey(KEY_PREFIX + jti));
        } catch (DataAccessException ex) {
            log.warn("Redis token blacklist check failed: {}", ex.getMessage());
            return false;
        }
    }
}
