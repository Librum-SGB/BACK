package com.sgb.mylibrum.exceptions;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Formato padrão de erro. `fieldErrors` aparece em erros de validação.")
public record ApiError(
        @Schema(description = "Instante em que o erro foi gerado.", example = "2026-11-03T19:30:00-03:00")
        OffsetDateTime timestamp,
        @Schema(description = "Código HTTP numérico.", example = "400")
        int status,
        @Schema(description = "Descrição resumida do status HTTP.", example = "Bad Request")
        String error,
        @Schema(description = "Mensagem explicativa do erro.", example = "Dados inválidos")
        String message,
        @Schema(description = "Caminho da requisição que gerou o erro.", example = "/api/editoras")
        String path,
        @Schema(description = "Mensagens de validação indexadas pelo nome do campo.")
        Map<String, String> fieldErrors) {

    public static ApiError of(HttpStatus status, String message, String path) {
        return new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, path, null);
    }

    public static ApiError of(HttpStatus status, String message, String path, Map<String, String> fieldErrors) {
        return new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
    }

    // Usado fora do MVC (filtros de segurança), onde o Jackson não está disponível de forma simples.
    public String toJson() {
        return "{\"timestamp\":\"" + timestamp + "\",\"status\":" + status
                + ",\"error\":\"" + escape(error) + "\",\"message\":\"" + escape(message)
                + "\",\"path\":\"" + escape(path) + "\"}";
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
