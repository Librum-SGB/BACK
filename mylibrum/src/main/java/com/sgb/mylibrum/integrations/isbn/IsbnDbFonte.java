package com.sgb.mylibrum.integrations.isbn;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.JsonNode;

/** Fonte paga: só é consultada quando a chave está nas configurações da filial. */
@Component
@Order(4)
public class IsbnDbFonte implements FonteIsbn {

    private final RestClient client;
    private final String baseUrl;

    public IsbnDbFonte(@Qualifier("isbnRestClient") RestClient client,
            @Value("${isbn.isbndb.url:https://api2.isbndb.com/book}") String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    @Override
    public String nome() {
        return "ISBNDB";
    }

    @Override
    public boolean disponivel(ChavesIsbn chaves) {
        return chaves != null && ChavesIsbn.presente(chaves.isbndb());
    }

    @Override
    public Optional<LivroExterno> buscar(String isbn, ChavesIsbn chaves) {
        String corpo = client.get().uri(baseUrl + "/{isbn}", isbn)
                .header("Authorization", chaves.isbndb()).retrieve().body(String.class);
        return interpretar(corpo, isbn);
    }

    Optional<LivroExterno> interpretar(String corpo, String isbn) {
        if (corpo == null) {
            return Optional.empty();
        }
        JsonNode livro = JsonUtil.ler(corpo).get("book");
        if (livro == null || livro.isNull()) {
            return Optional.empty();
        }
        LivroExterno resultado = new LivroExterno(isbn, JsonUtil.texto(livro, "title"), null,
                JsonUtil.textos(livro.get("authors"), null), JsonUtil.texto(livro, "publisher"),
                JsonUtil.ano(JsonUtil.texto(livro, "date_published")), JsonUtil.inteiro(livro, "pages"),
                JsonUtil.texto(livro, "synopsis"), JsonUtil.textos(livro.get("subjects"), null),
                JsonUtil.texto(livro, "language"), JsonUtil.texto(livro, "image"), nome());
        return resultado.utilizavel() ? Optional.of(resultado) : Optional.empty();
    }
}
