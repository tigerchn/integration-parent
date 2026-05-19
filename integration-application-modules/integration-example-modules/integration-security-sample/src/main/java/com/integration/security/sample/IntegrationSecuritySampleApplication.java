package com.integration.security.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 安全 starter 示例应用：演示公开与受保护接口。
 */
@SpringBootApplication
public class IntegrationSecuritySampleApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationSecuritySampleApplication.class, args);
    }
}
