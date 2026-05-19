package com.integration.encryptdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 独立进程：演示 {@code integration-common-encrypt-starter} 的请求解密与响应加密。
 */
@SpringBootApplication
public class IntegrationEncryptDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationEncryptDemoApplication.class, args);
    }
}
