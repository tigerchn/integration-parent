package com.integration.client.openapi;

import com.integration.common.knife4j.autoconfigure.Knife4jIntegrationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ClientOpenApiConfiguration {

    @Bean
    @Primary
    public OpenAPI clientOpenApi(Knife4jIntegrationProperties openapi) {
        return new OpenAPI()
                .info(new Info().title(openapi.getTitle()).version(openapi.getVersion()))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
