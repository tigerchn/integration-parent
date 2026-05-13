package com.integration.common.mq.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 消息队列集成自动配置：在存在 {@link ObjectMapper} 时注册 JSON 消息转换器。
 */
@AutoConfiguration(after = org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class)
@ConditionalOnClass(Jackson2JsonMessageConverter.class)
public class IntegrationMqAutoConfiguration {

    /**
     * @param objectMapper 用于 AMQP 消息体 JSON 序列化
     * @return Jackson2 JSON 消息转换器
     */
    @Bean
    @ConditionalOnBean(ObjectMapper.class)
    @ConditionalOnMissingBean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
