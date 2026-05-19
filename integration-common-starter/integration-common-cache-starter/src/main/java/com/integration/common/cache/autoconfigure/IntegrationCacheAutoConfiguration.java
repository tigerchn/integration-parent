package com.integration.common.cache.autoconfigure;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 启用 Spring Cache 注解；{@link CacheManager} 由 Boot 的 Caffeine 自动配置提供。
 * <p>必须在 {@link CacheAutoConfiguration} 之前处理，以满足其对 {@code CacheAspectSupport} Bean 的条件。
 */
@AutoConfiguration(before = CacheAutoConfiguration.class)
@ConditionalOnClass({CacheManager.class, Caffeine.class})
@EnableCaching
@EnableConfigurationProperties(IntegrationCacheProperties.class)
@ConditionalOnProperty(prefix = "integration.cache", name = "enabled", havingValue = "true", matchIfMissing = false)
public class IntegrationCacheAutoConfiguration {
}
