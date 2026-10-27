package com.sgb.mylibrum.integrations.isbn;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import tools.jackson.databind.JsonNode;

@Component
@Order(1)
public class GoogleBooksFonte implements FonteIsbn {

    private final RestClient client;
    private final String baseUrl;

    public GoogleBooksFonte(@Qualifier("isbnRestClient") RestClient client,
            @Value("${isbn.google.url:https://www.googleapis.com/books/v1/volumes}") String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    @Override
    public String nome() {
        return "GOOGLE_BOOKS";
    }

    @Override
    public boolean disponivel(ChavesIsbn chaves) {
        return true;
    }

    @Override
    public Optional<LivroExterno> buscar(String isbn, ChavesIsbn chaves) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(baseUrl).queryParam("q", "isbn:" + isbn);
        if (chaves != null && ChavesIsbn.presente(chaves.google())) {
            uri.queryParam("key", chaves.google());
        }
        String corpo = client.get().uri(uri.build().toUri()).retrieve().body(String.class);
        return interpretar(corpo, isbn);
    }

    Optional<LivroExterno> interpretar(String corpo, String isbn) {
        if (corpo == null) {
            return Optional.empty();
        }
        JsonNode itens = JsonUtil.ler(corpo).get("items");
        if (itens == null || !itens.isArray() || itens.isEmpty()) {
            return Optional.empty();
        }
        JsonNode info = itens.get(0).get("volumeInfo");
        if (info == null) {
            return Optional.empty();
        }
        JsonNode imagens = info.get("imageLinks");
        LivroExterno livro = new LivroExterno(isbn, JsonUtil.texto(info, "title"), JsonUtil.texto(info, "subtitle"),
                JsonUtil.textos(info.get("authors"), null), JsonUtil.texto(info, "publisher"),
                JsonUtil.ano(JsonUtil.texto(info, "publishedDate")), JsonUtil.inteiro(info, "pageCount"),
                JsonUtil.texto(info, "description"), JsonUtil.textos(info.get("categories"), null),
                JsonUtil.texto(info, "language"), JsonUtil.texto(imagens, "thumbnail"), nome());
        return livro.utilizavel() ? Optional.of(livro) : Optional.empty();
    }
}
