package com.sgb.mylibrum.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myLibrumOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MyLibrum API")
                        .description("API REST para o Sistema de Gestão de Bibliotecas (MyLibrum)")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Suporte SGB")
                                .email("suporte@sgbmylibrum.com")));
    }
}