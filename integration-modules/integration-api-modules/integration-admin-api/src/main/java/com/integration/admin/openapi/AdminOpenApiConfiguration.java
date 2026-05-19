package com.integration.admin.openapi;

import com.integration.common.knife4j.autoconfigure.Knife4jIntegrationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 管理端 OpenAPI 文档定义：标题版本与 Bearer JWT 安全方案。
 */
@Configuration
public class AdminOpenApiConfiguration {

    /**
     * @param openapi Knife4j/OpenAPI 展示用标题与版本
     * @return 主 {@link OpenAPI} Bean
     */
    @Bean
    @Primary
    public OpenAPI adminOpenApi(Knife4jIntegrationProperties openapi) {
        return new OpenAPI()
                .info(new Info().title(openapi.getTitle()).version(openapi.getVersion()))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
