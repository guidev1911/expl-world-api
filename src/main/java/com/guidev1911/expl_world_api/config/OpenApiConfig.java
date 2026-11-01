
package com.guidev1911.expl_world_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Informe o token JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI explWorldOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EXPL WORLD API")
                        .description("API for EXPL WORLD educational content platform")
                        .version("1.0.0"));
    }
}
