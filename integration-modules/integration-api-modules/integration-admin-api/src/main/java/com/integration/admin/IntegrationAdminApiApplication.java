package com.integration.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理端 API 应用入口，扫描 MyBatis Mapper。
 */
@SpringBootApplication
@MapperScan("com.integration.admin.rbac.mapper")
public class IntegrationAdminApiApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationAdminApiApplication.class, args);
    }
}