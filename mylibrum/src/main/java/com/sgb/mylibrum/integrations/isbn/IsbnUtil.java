package com.sgb.mylibrum.integrations.isbn;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class IsbnUtil {

    private IsbnUtil() {
    }

    /** Remove hífens/espaços, converte ISBN-10 em ISBN-13 e valida o dígito verificador. Lança 400 se inválido. */
    public static String normalizar(String bruto) {
        if (bruto == null) {
            throw invalido();
        }
        String s = bruto.replaceAll("[\\s-]", "").toUpperCase();
        if (s.matches("\\d{9}[\\dX]")) {
            if (!validoIsbn10(s)) {
                throw invalido();
            }
            return converter10Para13(s);
        }
        if (s.matches("\\d{13}") && validoIsbn13(s)) {
            return s;
        }
        throw invalido();
    }

    static boolean validoIsbn10(String s) {
        int soma = 0;
        for (int i = 0; i < 10; i++) {
            char c = s.charAt(i);
            int v = c == 'X' ? 10 : c - '0';
            soma += v * (10 - i);
        }
        return soma % 11 == 0;
    }

    static boolean validoIsbn13(String s) {
        int soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += (s.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return soma % 10 == 0;
    }

    static String converter10Para13(String isbn10) {
        String base = "978" + isbn10.substring(0, 9);
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += (base.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return base + ((10 - soma % 10) % 10);
    }

    private static ResponseStatusException invalido() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN inválido");
    }
}
