package com.sgb.mylibrum.services;

import java.util.Set;

public final class ConfiguracaoChaves {

    public static final String SMTP_EMAIL = "smtp.email";
    public static final String SMTP_SENHA = "smtp.senha";
    public static final String SMTP_HOST = "smtp.host";
    public static final String SMTP_PORTA = "smtp.porta";

    public static final String ISBN_GOOGLE_APIKEY = "isbn.google.apikey";
    public static final String ISBN_ISBNDB_APIKEY = "isbn.isbndb.apikey";
    public static final String ISBN_WORLDCAT_APIKEY = "isbn.worldcat.apikey";
    public static final String ISBN_WORLDCAT_SECRET = "isbn.worldcat.secret";

    public static final String MASCARA = "********";

    private static final Set<String> SENSIVEIS = Set.of(SMTP_SENHA, ISBN_GOOGLE_APIKEY, ISBN_ISBNDB_APIKEY,
            ISBN_WORLDCAT_APIKEY, ISBN_WORLDCAT_SECRET);

    private ConfiguracaoChaves() {
    }

    public static boolean ehSensivel(String chave) {
        return chave != null && SENSIVEIS.contains(chave);
    }
}
