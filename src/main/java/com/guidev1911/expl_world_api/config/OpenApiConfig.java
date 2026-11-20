package com.guidev1911.expl_world_api.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OperationCustomizer pageableCustomizer() {
        return (operation, handlerMethod) -> {

            if (operation.getParameters() != null) {
                operation.getParameters().removeIf(parameter ->
                        "sort".equals(parameter.getName())
                );
            }

            operation.addParametersItem(
                    new Parameter()
                            .name("sort")
                            .in("query")
                            .description("Ordenação: campo,direção. Exemplo: name,asc")
                            .example("name,asc")
                            .required(false)
                            .schema(new StringSchema())
            );

            return operation;
        };
    }
}