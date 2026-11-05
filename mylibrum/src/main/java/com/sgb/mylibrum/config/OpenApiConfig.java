package com.sgb.mylibrum.config;

import com.sgb.mylibrum.exceptions.ApiError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.customizers.OpenApiCustomizer;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String API_ERROR_SCHEMA = "#/components/schemas/ApiError";

    @Bean
    public OpenAPI myLibrumOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("MyLibrum API")
                        .description("""
                                API REST para gerenciamento de bibliotecas, acervo, usuários, empréstimos,
                                multas e tarefas. As datas são representadas em ISO-8601.

                                Endpoints `/api/**` exigem autenticação Bearer JWT, exceto quando indicado
                                de outra forma. Os endpoints `/auth/**` são públicos. Quando uma operação
                                falha, a API retorna `ApiError`; erros de validação podem incluir o mapa
                                `fieldErrors` com os campos inválidos.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Suporte SGB")
                                .email("suporte@sgbmylibrum.com")))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)));
    }

    @Bean
    public OpenApiCustomizer commonApiResponses() {
        return openApi -> {
            ModelConverters.getInstance().read(ApiError.class)
                    .forEach((name, schema) -> openApi.getComponents().addSchemas(name, schema));

            openApi.getPaths().forEach((path, pathItem) -> pathItem.readOperationsMap()
                    .forEach((method, operation) -> {
                        addResponse(operation, "400", "Parâmetros, corpo ou dados de entrada inválidos.");
                        addResponse(operation, "500", "Erro interno inesperado.");

                        if (path.startsWith("/api/")) {
                            addResponse(operation, "401", "Autenticação Bearer JWT necessária.");
                            addResponse(operation, "403", "Perfil ou usuário autenticado sem permissão para a operação.");
                        }
                        if (path.contains("{id}")) {
                            addResponse(operation, "404", "Registro não encontrado.");
                        }
                        if (method == HttpMethod.POST || method == HttpMethod.PUT
                                || method == HttpMethod.PATCH || method == HttpMethod.DELETE) {
                            addResponse(operation, "409", "Conflito com uma restrição de integridade ou concorrência.");
                        }
                    }));
        };
    }

    private void addResponse(io.swagger.v3.oas.models.Operation operation, String status, String description) {
        if (operation.getResponses().containsKey(status)) {
            return;
        }
        operation.getResponses().addApiResponse(status, new ApiResponse()
                .description(description)
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>().$ref(API_ERROR_SCHEMA)))));
    }
}