package com.integration.tooldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 独立进程：通过 HTTP 验证 {@link com.integration.common.tool.order.OrderNoUtil} 订单号生成。
 */
@SpringBootApplication
public class IntegrationToolDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationToolDemoApplication.class, args);
    }
}
