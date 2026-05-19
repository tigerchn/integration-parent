package com.integration.lockdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用于在独立进程中通过 HTTP 验证 {@code @DistributedLock}（需 Redis + {@code integration.redisson.enabled=true}）。
 */
@SpringBootApplication
public class IntegrationLockDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationLockDemoApplication.class, args);
    }
}
