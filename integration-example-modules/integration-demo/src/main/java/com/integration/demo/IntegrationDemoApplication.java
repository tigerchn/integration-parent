package com.integration.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 演示聚合工程应用入口，用于联调公共 starter 能力。
 */
@SpringBootApplication
public class IntegrationDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationDemoApplication.class, args);
    }
}
