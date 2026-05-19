package com.integration.exceldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 独立进程：通过 HTTP 验证 {@link com.integration.common.excel.ExcelHelper} 导入与导出。
 */
@SpringBootApplication
public class IntegrationExcelDemoApplication {

    /**
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegrationExcelDemoApplication.class, args);
    }
}
