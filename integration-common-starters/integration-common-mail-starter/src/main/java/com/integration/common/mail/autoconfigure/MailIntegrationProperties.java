package com.integration.common.mail.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮件集成扩展配置，前缀 {@code integration.mail}。
 * <p>SMTP 连接、账号等仍使用 Spring Boot 标准项 {@code spring.mail.*}。
 */
@ConfigurationProperties(prefix = "integration.mail")
public class MailIntegrationProperties {

    /**
     * 为 {@code false} 时不注册 {@link com.integration.common.mail.IntegrationMailService} Bean。
     */
    private boolean enabled = true;

    /**
     * 发件人邮箱；未配置时使用 {@code spring.mail.username}（须为完整邮箱地址，即包含 {@code @}）。
     */
    private String fromAddress;

    /**
     * 可选的发件人显示名称（MIME Personal 部分）。
     */
    private String fromDisplayName;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromDisplayName() {
        return fromDisplayName;
    }

    public void setFromDisplayName(String fromDisplayName) {
        this.fromDisplayName = fromDisplayName;
    }
}
