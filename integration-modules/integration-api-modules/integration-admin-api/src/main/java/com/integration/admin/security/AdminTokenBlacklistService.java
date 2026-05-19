package com.integration.admin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 基于 Redis 的 JWT jti 黑名单，用于登出后拒绝仍在有效期内的令牌。
 */
@Service
public class AdminTokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(AdminTokenBlacklistService.class);

    private static final String KEY_PREFIX = "admin:security:jti:";

    private final ObjectProvider<StringRedisTemplate> redisTemplate;
    private final AdminSecurityProperties securityProperties;

    /**
     * @param redisTemplate      可选的 {@link StringRedisTemplate}，不存在则黑名单不生效
     * @param securityProperties 是否启用 Redis 黑名单
     */
    public AdminTokenBlacklistService(ObjectProvider<StringRedisTemplate> redisTemplate,
                                      AdminSecurityProperties securityProperties) {
        this.redisTemplate = redisTemplate;
        this.securityProperties = securityProperties;
    }

    /**
     * 将 jti 写入 Redis，TTL 与令牌剩余有效期一致。
     *
     * @param jti JWT ID
     * @param ttl 存活时间
     */
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
            log.warn("Redis token blacklist add skipped (Redis unavailable): {}", ex.getMessage());
        }
    }

    /**
     * @param jti JWT ID
     * @return 若已加入黑名单则为 {@code true}
     */
    public boolean isBlocked(String jti) {
        StringRedisTemplate redis = redisTemplate.getIfAvailable();
        if (redis == null || !securityProperties.isRedisTokenBlacklistEnabled() || jti == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(redis.hasKey(KEY_PREFIX + jti));
        } catch (DataAccessException ex) {
            log.warn("Redis token blacklist check failed, treating as not blocked: {}", ex.getMessage());
            return false;
        }
    }
}
