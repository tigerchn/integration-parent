package com.integration.common.mail.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 邮件能力自动配置占位：启用 {@link MailIntegrationProperties} 绑定。
 */
@AutoConfiguration
@ConditionalOnClass(JavaMailSender.class)
@EnableConfigurationProperties(MailIntegrationProperties.class)
public class IntegrationMailAutoConfiguration {
}
