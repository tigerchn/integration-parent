package com.integration.common.mail.autoconfigure;

import com.integration.common.mail.IntegrationMailService;
import com.integration.common.mail.support.DefaultIntegrationMailService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * 在 Spring Boot 已装配 {@link JavaMailSender} 时注册 {@link IntegrationMailService}。
 */
@AutoConfiguration(after = MailSenderAutoConfiguration.class)
@ConditionalOnClass(JavaMailSender.class)
@EnableConfigurationProperties(MailIntegrationProperties.class)
public class IntegrationMailAutoConfiguration {

    /**
     * @param javaMailSender          由 {@link MailSenderAutoConfiguration} 提供（需配置 {@code spring.mail.*}）
     * @param integrationProperties   {@code integration.mail.*}
     * @param mailPropertiesProvider  {@code spring.mail.*} 绑定结果，用于默认发件人
     * @return 对外发信门面
     */
    @Bean
    @ConditionalOnBean(JavaMailSender.class)
    @ConditionalOnProperty(prefix = "integration.mail", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(IntegrationMailService.class)
    public IntegrationMailService integrationMailService(JavaMailSender javaMailSender,
                                                         MailIntegrationProperties integrationProperties,
                                                         ObjectProvider<MailProperties> mailPropertiesProvider) {
        MailProperties mailProperties = mailPropertiesProvider.getIfAvailable();
        return new DefaultIntegrationMailService(javaMailSender, integrationProperties, mailProperties);
    }
}
