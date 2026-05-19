package com.integration.common.cache.env;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * 在未显式配置时补充 {@code spring.cache.*}，便于引入 starter 即可使用 Caffeine；
 * 优先级低于 {@code application.yml} / 环境变量（使用 {@code addLast}）。
 */
public class IntegrationCachingEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String PROPERTY_SOURCE_NAME = "integration-cache-defaults";

    private static final String INTEGRATION_CACHE_ENABLED = "integration.cache.enabled";
    private static final String INTEGRATION_CACHE_PREFER_CAFFEINE = "integration.cache.prefer-caffeine";
    private static final String SPRING_CACHE_TYPE = "spring.cache.type";
    private static final String SPRING_CACHE_CAFFEINE_SPEC = "spring.cache.caffeine.spec";

    /**
     * 与 Boot {@link org.springframework.boot.autoconfigure.cache.CacheProperties} 中常用默认相当，可被业务覆盖。
     */
    private static final String DEFAULT_CAFFEINE_SPEC = "maximumSize=10000,expireAfterWrite=10m";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!environment.getProperty(INTEGRATION_CACHE_ENABLED, Boolean.class, Boolean.TRUE)) {
            return;
        }
        Map<String, Object> defaults = new HashMap<>(2);
        if (environment.getProperty(INTEGRATION_CACHE_PREFER_CAFFEINE, Boolean.class, Boolean.TRUE)
                && environment.getProperty(SPRING_CACHE_TYPE) == null) {
            defaults.put(SPRING_CACHE_TYPE, "caffeine");
        }
        if (environment.getProperty(SPRING_CACHE_CAFFEINE_SPEC) == null) {
            defaults.put(SPRING_CACHE_CAFFEINE_SPEC, DEFAULT_CAFFEINE_SPEC);
        }
        if (defaults.isEmpty()) {
            return;
        }
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, defaults));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
