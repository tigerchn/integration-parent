package com.integration.storagedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 联调腾讯云 COS / 阿里云 OSS 上传与预签名下载。
 */
@SpringBootApplication
public class IntegrationStorageDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationStorageDemoApplication.class, args);
    }
}
