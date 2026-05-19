package com.integration.common.web.autoconfigure;

import com.integration.common.web.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * Web 层自动配置：注册全局异常处理等 Servlet Web 组件。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebAutoConfiguration {

    /**
     * @return 将各类异常映射为 {@link com.integration.common.core.api.ApiResult} 的全局处理器
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
