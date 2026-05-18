package com.integration.cachedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 独立进程：通过 HTTP 验证本地 Caffeine 与 {@link org.springframework.cache.annotation.Cacheable}。
 */
@SpringBootApplication
public class IntegrationCacheDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationCacheDemoApplication.class, args);
    }
}
