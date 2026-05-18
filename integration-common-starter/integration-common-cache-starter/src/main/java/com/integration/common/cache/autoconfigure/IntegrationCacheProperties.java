package com.integration.common.cache.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本地缓存（Caffeine）装配开关；具体容量与过期仍使用 Boot 标准 {@code spring.cache.*}。
 */
@ConfigurationProperties(prefix = "integration.cache")
public class IntegrationCacheProperties {

    /**
     * 是否启用 {@link org.springframework.cache.annotation.EnableCaching} 及默认属性注入。
     */
    private boolean enabled = true;

    /**
     * 当未显式配置 {@code spring.cache.type} 时，是否默认使用 {@code caffeine}。
     * <p>若需使用 Redis 等作为 {@link org.springframework.cache.CacheManager}，请设为 {@code false} 并配置
     * {@code spring.cache.type}。
     */
    private boolean preferCaffeine = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPreferCaffeine() {
        return preferCaffeine;
    }

    public void setPreferCaffeine(boolean preferCaffeine) {
        this.preferCaffeine = preferCaffeine;
    }
}
