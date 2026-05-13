package com.integration.common.knife4j.autoconfigure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 默认 OpenAPI 文档 Bean 自动配置（标题与版本来自配置）。
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@EnableConfigurationProperties(Knife4jIntegrationProperties.class)
public class IntegrationKnife4jAutoConfiguration {

    /**
     * @param properties 文档标题与版本
     * @return 默认 {@link OpenAPI}，可被业务模块覆盖
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI integrationOpenApi(Knife4jIntegrationProperties properties) {
        return new OpenAPI().info(new Info().title(properties.getTitle()).version(properties.getVersion()));
    }
}
