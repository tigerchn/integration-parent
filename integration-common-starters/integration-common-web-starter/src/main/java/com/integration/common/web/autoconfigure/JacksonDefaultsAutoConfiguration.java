package com.integration.common.web.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.web.jackson.IntegrationJacksonDefaultsCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 注册平台级 {@link org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer}，
 * 作用于容器中的 {@link ObjectMapper} 与 MVC 消息转换器。
 */
@AutoConfiguration(before = JacksonAutoConfiguration.class)
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JacksonDefaultsAutoConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Jackson2ObjectMapperBuilderCustomizer integrationJacksonDefaultsCustomizer() {
        return new IntegrationJacksonDefaultsCustomizer();
    }
}
