package com.integration.common.knife4j.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据 {@code integration.openapi.enabled} 关闭文档，或在未显式配置时补充 springdoc / knife4j 默认值。
 */
public class IntegrationOpenApiEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String PROPERTY_SOURCE_NAME = "integration-openapi-defaults";

    private static final String INTEGRATION_OPENAPI_ENABLED = "integration.openapi.enabled";
    private static final String SPRINGDOC_SWAGGER_UI_PATH = "springdoc.swagger-ui.path";
    private static final String KNIFE4J_ENABLE = "knife4j.enable";
    private static final String SPRINGDOC_API_DOCS_ENABLED = "springdoc.api-docs.enabled";
    private static final String SPRINGDOC_SWAGGER_UI_ENABLED = "springdoc.swagger-ui.enabled";

    private static final String DEFAULT_SWAGGER_UI_PATH = "/swagger-ui.html";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean enabled = environment.getProperty(INTEGRATION_OPENAPI_ENABLED, Boolean.class, Boolean.TRUE);
        if (!enabled) {
            Map<String, Object> disabled = new HashMap<>(3);
            disabled.put(KNIFE4J_ENABLE, false);
            disabled.put(SPRINGDOC_API_DOCS_ENABLED, false);
            disabled.put(SPRINGDOC_SWAGGER_UI_ENABLED, false);
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME + "-off", disabled));
            return;
        }

        Map<String, Object> defaults = new HashMap<>(2);
        if (environment.getProperty(SPRINGDOC_SWAGGER_UI_PATH) == null) {
            defaults.put(SPRINGDOC_SWAGGER_UI_PATH, DEFAULT_SWAGGER_UI_PATH);
        }
        if (environment.getProperty(KNIFE4J_ENABLE) == null) {
            defaults.put(KNIFE4J_ENABLE, true);
        }
        if (!defaults.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, defaults));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
