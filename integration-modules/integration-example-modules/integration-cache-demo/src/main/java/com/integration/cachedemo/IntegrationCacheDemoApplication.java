package com.integration.cachedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * 独立进程：通过 HTTP 验证本地 Caffeine 与 {@link org.springframework.cache.annotation.Cacheable}。
 * <p>
 * 仅扫描本模块 {@link Configuration}（演示 Controller/Service 由 {@link config.CacheDemoConfiguration} 按条件注册）。
 */
@SpringBootApplication
@ComponentScan(
        basePackages = "com.integration.cachedemo",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class),
        useDefaultFilters = false)
public class IntegrationCacheDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationCacheDemoApplication.class, args);
    }
}
