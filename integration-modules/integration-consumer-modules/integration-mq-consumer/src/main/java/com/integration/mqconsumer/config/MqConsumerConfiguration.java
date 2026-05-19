package com.integration.mqconsumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MqConsumerProperties.class)
@ConditionalOnProperty(prefix = "integration.mq.consumer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MqConsumerConfiguration {

    @Bean
    Queue jobTaskQueue(MqConsumerProperties properties) {
        return new Queue(properties.getQueue(), true);
    }

    @Bean
    DirectExchange jobTaskExchange(MqConsumerProperties properties) {
        return new DirectExchange(properties.getExchange(), true, false);
    }

    @Bean
    Binding jobTaskBinding(Queue jobTaskQueue, DirectExchange jobTaskExchange, MqConsumerProperties properties) {
        return BindingBuilder.bind(jobTaskQueue)
                .to(jobTaskExchange)
                .with(properties.getRoutingKey());
    }
}
