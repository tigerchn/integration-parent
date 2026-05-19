package com.integration.cachedemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 验证 {@code integration.cache.enabled=false} 时应用仍可启动（缓存演示 Bean 不装配）。
 */
@SpringBootTest(properties = "integration.cache.enabled=false")
class IntegrationCacheDemoDisabledCacheTests {

    @Test
    void contextLoadsWhenCacheDisabled() {
    }
}
