package com.integration.common.mail.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮件集成扩展配置，前缀 {@code integration.mail}。
 */
@ConfigurationProperties(prefix = "integration.mail")
public class MailIntegrationProperties {

    /**
     * 可选的发件人显示名称（业务层可自行使用）。
     */
    private String fromDisplayName;

    public String getFromDisplayName() {
        return fromDisplayName;
    }

    public void setFromDisplayName(String fromDisplayName) {
        this.fromDisplayName = fromDisplayName;
    }
}
