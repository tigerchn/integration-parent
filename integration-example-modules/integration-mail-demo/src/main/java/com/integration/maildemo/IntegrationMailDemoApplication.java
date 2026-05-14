package com.integration.maildemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 独立进程：通过 HTTP 联调 {@link com.integration.common.mail.IntegrationMailService}。
 */
@SpringBootApplication
public class IntegrationMailDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationMailDemoApplication.class, args);
    }
}
