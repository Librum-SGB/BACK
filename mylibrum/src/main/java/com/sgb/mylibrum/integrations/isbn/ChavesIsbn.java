package com.sgb.mylibrum.integrations.isbn;

/** Credenciais das fontes externas, lidas das configurações da filial. Valores ausentes são nulos. */
public record ChavesIsbn(String google, String isbndb, String worldcatKey, String worldcatSecret) {

    public static final ChavesIsbn VAZIAS = new ChavesIsbn(null, null, null, null);

    public static boolean presente(String valor) {
        return valor != null && !valor.isBlank();
    }
}
