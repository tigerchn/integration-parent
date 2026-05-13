package com.integration.common.tool.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.common.tool.json.Jsons;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 工具类 Bean 自动配置（如 {@link Jsons}）。
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class ToolAutoConfiguration {

    /**
     * @param objectMapper 由 Spring Boot Jackson 自动配置提供
     * @return JSON 工具 Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public Jsons jsons(ObjectMapper objectMapper) {
        return new Jsons(objectMapper);
    }
}
