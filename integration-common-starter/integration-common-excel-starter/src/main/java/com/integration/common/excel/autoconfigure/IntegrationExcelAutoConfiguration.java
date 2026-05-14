package com.integration.common.excel.autoconfigure;

import com.alibaba.excel.EasyExcel;
import com.integration.common.excel.ExcelHelper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 注册 {@link ExcelHelper}，供业务模块注入后进行 Excel 与实体之间的转换。
 */
@AutoConfiguration
@ConditionalOnClass(EasyExcel.class)
public class IntegrationExcelAutoConfiguration {

    /**
     * @return Excel 读写助手
     */
    @Bean
    @ConditionalOnMissingBean(ExcelHelper.class)
    public ExcelHelper excelHelper() {
        return new ExcelHelper();
    }
}
