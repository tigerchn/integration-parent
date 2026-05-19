package com.integration.client;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 客户端（App）API 应用入口。
 */
@SpringBootApplication
@MapperScan("com.integration.client.user.mapper")
public class IntegrationClientApiApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationClientApiApplication.class, args);
    }
}
