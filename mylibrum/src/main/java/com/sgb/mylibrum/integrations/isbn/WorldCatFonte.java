package com.sgb.mylibrum.integrations.isbn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import tools.jackson.databind.JsonNode;

/**
 * WorldCat Search API v2 (OCLC). Exige chave (WSKey) e segredo nas configurações da filial.
 * Autenticação OAuth2 client_credentials com escopo "wcapi".
 */
@Component
@Order(5)
public class WorldCatFonte implements FonteIsbn {

    private final RestClient client;
    private final String tokenUrl;
    private final String buscaUrl;

    public WorldCatFonte(@Qualifier("isbnRestClient") RestClient client,
            @Value("${isbn.worldcat.token-url:https://oauth.oclc.org/token}") String tokenUrl,
            @Value("${isbn.worldcat.url:https://americas.discovery.api.oclc.org/worldcat/search/v2/bibs}")
            String buscaUrl) {
        this.client = client;
        this.tokenUrl = tokenUrl;
        this.buscaUrl = buscaUrl;
    }

    @Override
    public String nome() {
        return "WORLDCAT";
    }

    @Override
    public boolean disponivel(ChavesIsbn chaves) {
        return chaves != null && ChavesIsbn.presente(chaves.worldcatKey())
                && ChavesIsbn.presente(chaves.worldcatSecret());
    }

    @Override
    public Optional<LivroExterno> buscar(String isbn, ChavesIsbn chaves) {
        String token = obterToken(chaves);
        if (token == null) {
            return Optional.empty();
        }
        String corpo = client.get()
                .uri(UriComponentsBuilder.fromUriString(buscaUrl).queryParam("q", "bn:" + isbn).build().toUri())
                .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)
                .retrieve().body(String.class);
        return interpretar(corpo, isbn);
    }

    private String obterToken(ChavesIsbn chaves) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("scope", "wcapi");
        String corpo = client.post().uri(tokenUrl)
                .headers(h -> h.setBasicAuth(chaves.worldcatKey(), chaves.worldcatSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().body(String.class);
        return corpo == null ? null : JsonUtil.texto(JsonUtil.ler(corpo), "access_token");
    }

    Optional<LivroExterno> interpretar(String corpo, String isbn) {
        if (corpo == null) {
            return Optional.empty();
        }
        JsonNode registros = JsonUtil.ler(corpo).get("bibRecords");
        if (registros == null || !registros.isArray() || registros.isEmpty()) {
            return Optional.empty();
        }
        JsonNode reg = registros.get(0);
        String titulo = null;
        JsonNode titulos = reg.path("title").get("mainTitles");
        if (titulos != null && titulos.isArray() && !titulos.isEmpty()) {
            titulo = JsonUtil.texto(titulos.get(0), "text");
        }
        List<String> autores = new ArrayList<>();
        JsonNode criadores = reg.path("contributor").get("creators");
        if (criadores != null && criadores.isArray()) {
            for (JsonNode c : criadores) {
                String nome = JsonUtil.texto(c.get("nonPersonName"), "text");
                if (nome == null) {
                    String primeiro = JsonUtil.texto(c.get("firstName"), "text");
                    String segundo = JsonUtil.texto(c.get("secondName"), "text");
                    nome = ((primeiro == null ? "" : primeiro) + " " + (segundo == null ? "" : segundo)).trim();
                }
                if (!nome.isEmpty()) {
                    autores.add(nome);
                }
            }
        }
        String editora = null;
        JsonNode editoras = reg.path("publishing").get("publisherName");
        if (editoras != null && editoras.isValueNode()) {
            editora = editoras.asString().trim();
        } else if (editoras != null && editoras.isArray() && !editoras.isEmpty()) {
            editora = JsonUtil.texto(editoras.get(0), "text");
        }
        Integer ano = JsonUtil.ano(JsonUtil.texto(reg.path("date"), "publicationDate"));
        LivroExterno resultado = new LivroExterno(isbn, titulo, null, autores, editora, ano, null, null, null, null,
                null, nome());
        return resultado.utilizavel() ? Optional.of(resultado) : Optional.empty();
    }
}
