package com.integration.common.encrypt.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.encrypt.config.EncryptProperties;
import com.integration.common.encrypt.core.DecryptOncePerRequestFilter;
import com.integration.common.encrypt.core.EncryptResponseBodyAdvice;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import java.util.EnumSet;
import java.util.List;

@Configuration
@EnableConfigurationProperties(EncryptProperties.class)
@ConditionalOnProperty(prefix = "integration.encrypt", name = "enable", havingValue = "true", matchIfMissing = false)
public class IntegrationEncryptAutoConfiguration {

    /**
     * 启动时校验必要配置，避免运行期才暴露缺密钥问题。
     */
    @Bean
    static EncryptStartupValidator encryptStartupValidator(EncryptProperties properties) {
        return new EncryptStartupValidator(properties);
    }

    @Bean
    public FilterRegistrationBean<DecryptOncePerRequestFilter> decryptRequestFilterRegistration(
            EncryptProperties properties,
            ObjectMapper objectMapper,
            List<HandlerMapping> handlerMappings) {
        DecryptOncePerRequestFilter filter = new DecryptOncePerRequestFilter(properties, objectMapper, handlerMappings);
        FilterRegistrationBean<DecryptOncePerRequestFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }

    @Bean
    public EncryptResponseBodyAdvice encryptResponseBodyAdvice(EncryptProperties properties, ObjectMapper objectMapper) {
        return new EncryptResponseBodyAdvice(properties, objectMapper);
    }

    static final class EncryptStartupValidator {

        private EncryptStartupValidator(EncryptProperties properties) {
            if (!StringUtils.hasText(properties.getRsaPublicKey()) || !StringUtils.hasText(properties.getRsaPrivateKey())) {
                throw new IllegalStateException(
                        "integration.encrypt 已启用：请同时配置 integration.encrypt.rsa-public-key 与 integration.encrypt.rsa-private-key（Base64 DER）。");
            }
            int bits = properties.getAesKeySize();
            if (bits != 128 && bits != 192 && bits != 256) {
                throw new IllegalStateException(
                        "integration.encrypt.aes-key-size 必须为 128、192 或 256，当前值: " + bits);
            }
        }
    }
}
