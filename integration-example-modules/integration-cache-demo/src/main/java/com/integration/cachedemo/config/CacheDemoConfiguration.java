package com.integration.cachedemo.config;

import com.integration.cachedemo.service.CacheDemoService;
import com.integration.cachedemo.service.CacheDevService;
import com.integration.cachedemo.service.CacheTestService;
import com.integration.cachedemo.web.CacheDemoController;
import com.integration.common.cache.util.LocalCacheUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存演示 Bean 仅在 {@code integration.cache.enabled=true} 时注册（与 starter 开关一致）。
 */
@Configuration
@ConditionalOnProperty(prefix = "integration.cache", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CacheDemoConfiguration {

    @Bean
    public CacheDemoService cacheDemoService() {
        return new CacheDemoService();
    }

    @Bean
    public CacheDevService cacheDevService() {
        return new CacheDevService();
    }

    @Bean
    public CacheTestService cacheTestService() {
        return new CacheTestService();
    }

    @Bean
    public CacheDemoController cacheDemoController(CacheDemoService cacheDemoService, LocalCacheUtil localCacheUtil, CacheManager cacheManager) {
        return new CacheDemoController(cacheDemoService, localCacheUtil, cacheManager);
    }
}
