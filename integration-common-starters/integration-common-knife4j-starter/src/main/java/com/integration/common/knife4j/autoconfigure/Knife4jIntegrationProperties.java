package com.integration.common.knife4j.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenAPI / Knife4j 平台配置，前缀 {@code integration.openapi}。
 */
@ConfigurationProperties(prefix = "integration.openapi")
public class Knife4jIntegrationProperties {

    /**
     * 为 {@code true} 时启用 OpenAPI 文档与 Knife4j（默认开启）；为 {@code false} 时关闭 api-docs 与 UI。
     */
    private boolean enabled = true;

    private String title = "Integration API";

    private String version = "v1";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
