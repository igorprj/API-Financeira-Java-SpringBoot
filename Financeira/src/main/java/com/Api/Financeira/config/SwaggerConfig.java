package com.Api.Financeira.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securityScheme = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Financeira API")
                        .version("1.0")
                        .description("API para controle financeiro"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securityScheme))
                .schemaRequirement(
                        securityScheme,
                        new SecurityScheme()
                                .name(securityScheme)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")
                );

    }
}
