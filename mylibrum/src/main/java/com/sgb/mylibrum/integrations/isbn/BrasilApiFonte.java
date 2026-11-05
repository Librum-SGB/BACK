package com.sgb.mylibrum.integrations.isbn;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.JsonNode;

@Component
@Order(3)
public class BrasilApiFonte implements FonteIsbn {

    private final RestClient client;
    private final String baseUrl;

    public BrasilApiFonte(@Qualifier("isbnRestClient") RestClient client,
            @Value("${isbn.brasilapi.url:https://brasilapi.com.br/api/isbn/v1}") String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    @Override
    public String nome() {
        return "BRASIL_API";
    }

    @Override
    public boolean disponivel(ChavesIsbn chaves) {
        return true;
    }

    @Override
    public Optional<LivroExterno> buscar(String isbn, ChavesIsbn chaves) {
        String corpo = client.get().uri(baseUrl + "/{isbn}", isbn).retrieve().body(String.class);
        return interpretar(corpo, isbn);
    }

    Optional<LivroExterno> interpretar(String corpo, String isbn) {
        if (corpo == null) {
            return Optional.empty();
        }
        JsonNode livro = JsonUtil.ler(corpo);
        LivroExterno resultado = new LivroExterno(isbn, JsonUtil.texto(livro, "title"),
                JsonUtil.texto(livro, "subtitle"), JsonUtil.textos(livro.get("authors"), null),
                JsonUtil.texto(livro, "publisher"), JsonUtil.inteiro(livro, "year"),
                JsonUtil.inteiro(livro, "page_count"), JsonUtil.texto(livro, "synopsis"),
                JsonUtil.textos(livro.get("subjects"), null), null, JsonUtil.texto(livro, "cover_url"), nome());
        return resultado.utilizavel() ? Optional.of(resultado) : Optional.empty();
    }
}
