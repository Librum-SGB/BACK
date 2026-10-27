package com.sgb.mylibrum.specifications;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class FiltrosUtil {

    private FiltrosUtil() {
    }

    /** Sem valor = só ativos; "todos" = sem filtro; "true"/"false" = filtro explícito. */
    public static Boolean interpretarAtivo(String valor) {
        if (valor == null || valor.isBlank()) {
            return Boolean.TRUE;
        }
        return switch (valor.trim().toLowerCase(Locale.ROOT)) {
            case "true" -> Boolean.TRUE;
            case "false" -> Boolean.FALSE;
            case "todos" -> null;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Valor inválido para o parâmetro 'ativo' (use true, false ou todos)");
        };
    }

    public static String textoOuNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    public static <V extends Comparable<? super V>> void validarIntervalo(V de, V ate, String campo) {
        if (de != null && ate != null && de.compareTo(ate) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Intervalo inválido em '" + campo + "': o valor inicial é maior que o final");
        }
    }

    public static <T, V extends Comparable<? super V>> Specification<T> entre(String campo, V de, V ate) {
        return (root, query, cb) -> {
            if (de != null && ate != null) {
                return cb.between(root.<V>get(campo), de, ate);
            }
            if (de != null) {
                return cb.greaterThanOrEqualTo(root.<V>get(campo), de);
            }
            if (ate != null) {
                return cb.lessThanOrEqualTo(root.<V>get(campo), ate);
            }
            return cb.conjunction();
        };
    }
}
