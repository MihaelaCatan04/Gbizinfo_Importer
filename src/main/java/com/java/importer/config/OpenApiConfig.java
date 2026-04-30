package com.java.importer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public io.swagger.v3.oas.models.OpenAPI customOpenAPI() {
        return new io.swagger.v3.oas.models.OpenAPI().info(new io.swagger.v3.oas.models.info.Info().title("Importer Executor").version("1.0").description("API documentation for the Importer Microservice.")).addSecurityItem(new SecurityRequirement().addList("ImporterScheme")).components(new Components().addSecuritySchemes("Importer", new SecurityScheme().name("JavaTestScheme").type(SecurityScheme.Type.HTTP).scheme("basic")));
    }
}
