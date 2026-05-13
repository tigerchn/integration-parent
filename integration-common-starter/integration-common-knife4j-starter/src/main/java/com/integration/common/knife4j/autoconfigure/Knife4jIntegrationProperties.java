package com.integration.common.knife4j.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OpenAPI / Knife4j 展示用元数据，前缀 {@code integration.openapi}。
 */
@ConfigurationProperties(prefix = "integration.openapi")
public class Knife4jIntegrationProperties {

    private String title = "Integration API";

    private String version = "v1";

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
