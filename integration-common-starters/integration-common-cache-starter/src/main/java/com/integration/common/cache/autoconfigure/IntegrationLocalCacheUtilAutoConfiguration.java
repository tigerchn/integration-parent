package com.integration.common.cache.autoconfigure;

import com.integration.common.cache.util.LocalCacheUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

/**
 * 在 {@link CacheManager} 就绪后注册 {@link LocalCacheUtil}。
 */
@AutoConfiguration(after = CacheAutoConfiguration.class)
@ConditionalOnClass(CacheManager.class)
@ConditionalOnBean(CacheManager.class)
@ConditionalOnProperty(prefix = "integration.cache", name = "enabled", havingValue = "true", matchIfMissing = false)
public class IntegrationLocalCacheUtilAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LocalCacheUtil localCacheUtil(CacheManager cacheManager) {
        return new LocalCacheUtil(cacheManager);
    }
}
