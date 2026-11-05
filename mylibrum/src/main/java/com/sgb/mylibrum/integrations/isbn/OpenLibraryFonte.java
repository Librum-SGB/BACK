package com.sgb.mylibrum.integrations.isbn;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import tools.jackson.databind.JsonNode;

@Component
@Order(2)
public class OpenLibraryFonte implements FonteIsbn {

    private final RestClient client;
    private final String baseUrl;

    public OpenLibraryFonte(@Qualifier("isbnRestClient") RestClient client,
            @Value("${isbn.openlibrary.url:https://openlibrary.org/api/books}") String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    @Override
    public String nome() {
        return "OPEN_LIBRARY";
    }

    @Override
    public boolean disponivel(ChavesIsbn chaves) {
        return true;
    }

    @Override
    public Optional<LivroExterno> buscar(String isbn, ChavesIsbn chaves) {
        String corpo = client.get()
                .uri(UriComponentsBuilder.fromUriString(baseUrl).queryParam("bibkeys", "ISBN:" + isbn)
                        .queryParam("format", "json").queryParam("jscmd", "data").build().toUri())
                .retrieve().body(String.class);
        return interpretar(corpo, isbn);
    }

    Optional<LivroExterno> interpretar(String corpo, String isbn) {
        if (corpo == null) {
            return Optional.empty();
        }
        JsonNode livro = JsonUtil.ler(corpo).get("ISBN:" + isbn);
        if (livro == null || livro.isNull()) {
            return Optional.empty();
        }
        List<String> editoras = JsonUtil.textos(livro.get("publishers"), "name");
        Integer paginas = JsonUtil.inteiro(livro, "number_of_pages");
        JsonNode capa = livro.get("cover");
        String capaUrl = JsonUtil.texto(capa, "medium");
        LivroExterno resultado = new LivroExterno(isbn, JsonUtil.texto(livro, "title"),
                JsonUtil.texto(livro, "subtitle"), JsonUtil.textos(livro.get("authors"), "name"),
                editoras.isEmpty() ? null : editoras.get(0), JsonUtil.ano(JsonUtil.texto(livro, "publish_date")),
                paginas, null, JsonUtil.textos(livro.get("subjects"), "name").stream().limit(5).toList(), null,
                capaUrl, nome());
        return resultado.utilizavel() ? Optional.of(resultado) : Optional.empty();
    }
}
