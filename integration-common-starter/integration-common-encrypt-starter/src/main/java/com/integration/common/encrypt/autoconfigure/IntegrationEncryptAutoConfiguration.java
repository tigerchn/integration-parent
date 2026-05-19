package com.integration.common.encrypt.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.encrypt.assistant.PayloadAssistant;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.core.DecryptOncePerRequestFilter;
import com.integration.common.encrypt.core.EncryptResponseBodyAdvice;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.EnumSet;

/**
 * 装配策略：{@code decrypt-request-body-enabled=true} 才注册请求解密过滤器；
 * {@code encrypt-response-body-enabled=true} 才注册响应加密 {@code ControllerAdvice}；二者可独立开关。
 */
@AutoConfiguration(after = JacksonAutoConfiguration.class)
@EnableConfigurationProperties(EncryptProperties.class)
@ConditionalOnBean(ObjectMapper.class)
@ConditionalOnProperty(prefix = "integration.encrypt", name = "enable", havingValue = "true", matchIfMissing = false)
public class IntegrationEncryptAutoConfiguration {

    @Bean
    static EncryptStartupValidator encryptStartupValidator(EncryptProperties properties) {
        return new EncryptStartupValidator(properties);
    }

    @Bean
    PayloadAssistant payloadAssistant(EncryptProperties properties, ObjectMapper objectMapper) {
        return new PayloadAssistant(properties, objectMapper);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "integration.encrypt", name = "decrypt-request-body-enabled", havingValue = "true", matchIfMissing = false)
    static class DecryptFilterAutoConfiguration {

        @Bean
        FilterRegistrationBean<DecryptOncePerRequestFilter> decryptRequestFilterRegistration(
                EncryptProperties properties,
                PayloadAssistant payloadAssistant,
                ObjectMapper objectMapper) {
            DecryptOncePerRequestFilter filter =
                    new DecryptOncePerRequestFilter(properties, payloadAssistant, objectMapper);
            FilterRegistrationBean<DecryptOncePerRequestFilter> registration = new FilterRegistrationBean<>(filter);
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
            registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
            return registration;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "integration.encrypt", name = "encrypt-response-body-enabled", havingValue = "true", matchIfMissing = false)
    static class EncryptAdviceAutoConfiguration {

        @Bean
        EncryptResponseBodyAdvice encryptResponseBodyAdvice(EncryptProperties properties,
                                                              PayloadAssistant payloadAssistant) {
            return new EncryptResponseBodyAdvice(properties, payloadAssistant);
        }
    }

    static final class EncryptStartupValidator {

        private EncryptStartupValidator(EncryptProperties properties) {
            int bits = properties.getAesKeySize();
            if (bits != 128 && bits != 192 && bits != 256) {
                throw new IllegalStateException(
                        "integration.encrypt.aes-key-size 必须为 128、192 或 256，当前值: " + bits);
            }
            if (!properties.isDecryptRequestBodyEnabled() && !properties.isEncryptResponseBodyEnabled()) {
                return;
            }
            if (properties.isDecryptRequestBodyEnabled() && !StringUtils.hasText(properties.getRsaPrivateKey())) {
                throw new IllegalStateException(
                        "integration.encrypt.decrypt-request-body-enabled=true 时需配置 integration.encrypt.rsa-private-key。");
            }
            if (properties.isEncryptResponseBodyEnabled() && !StringUtils.hasText(properties.getRsaPublicKey())) {
                throw new IllegalStateException(
                        "integration.encrypt.encrypt-response-body-enabled=true 时需配置 integration.encrypt.rsa-public-key。");
            }
        }
    }
}
