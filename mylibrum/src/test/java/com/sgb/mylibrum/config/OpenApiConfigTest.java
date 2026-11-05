package com.sgb.mylibrum.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.responses.ApiResponses;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void definesBearerSchemeWithoutApplyingItToPublicEndpoints() {
        OpenAPI openApi = config.myLibrumOpenAPI();

        assertNotNull(openApi.getComponents().getSecuritySchemes().get("bearerAuth"));
        assertNull(openApi.getSecurity());
    }

    @Test
    void addsDocumentedErrorsToApiOperations() {
        Operation operation = new Operation().responses(new ApiResponses());
        OpenAPI openApi = config.myLibrumOpenAPI()
                .paths(new Paths().addPathItem("/api/editoras/{id}", new PathItem().get(operation)));

        config.commonApiResponses().customise(openApi);

        assertEquals("Autenticação Bearer JWT necessária.", operation.getResponses().get("401").getDescription());
        assertEquals("Registro não encontrado.", operation.getResponses().get("404").getDescription());
        assertEquals("Parâmetros, corpo ou dados de entrada inválidos.",
                operation.getResponses().get("400").getDescription());
        assertNotNull(openApi.getComponents().getSchemas().get("ApiError"));
        assertNotNull(operation.getResponses().get("401").getContent());
    }

    @Test
    void doesNotDocumentApiAuthenticationForPublicAuthOperations() {
        Operation operation = new Operation().responses(new ApiResponses());
        OpenAPI openApi = config.myLibrumOpenAPI()
                .paths(new Paths().addPathItem("/auth/login", new PathItem().post(operation)));

        config.commonApiResponses().customise(openApi);

        assertFalse(operation.getResponses().containsKey("401"));
        assertNotNull(operation.getResponses().get("400"));
    }
}
