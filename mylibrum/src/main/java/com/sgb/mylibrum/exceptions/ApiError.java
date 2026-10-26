package com.sgb.mylibrum.exceptions;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
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
