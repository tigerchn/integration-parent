package com.integration.mqconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RabbitMQ 异步任务消费端应用入口。
 */
@SpringBootApplication
public class IntegrationMqConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationMqConsumerApplication.class, args);
    }
}
