package com.integration.jobconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 本地定时任务（{@link org.springframework.scheduling.annotation.Scheduled}）消费端应用入口。
 */
@SpringBootApplication
public class IntegrationJobConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationJobConsumerApplication.class, args);
    }
}
