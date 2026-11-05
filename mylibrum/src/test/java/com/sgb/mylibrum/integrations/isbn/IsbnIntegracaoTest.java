package com.sgb.mylibrum.integrations.isbn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.sgb.mylibrum.repositories.ConfiguracaoRepository;

class IsbnIntegracaoTest {

    private static final String ISBN = "9780132350884";

    @Test
    void normalizaIsbn10ParaIsbn13() {
        assertEquals(ISBN, IsbnUtil.normalizar("0-13-235088-2"));
        assertEquals(ISBN, IsbnUtil.normalizar("978-0-13-235088-4"));
    }

    @Test
    void rejeitaIsbnInvalido() {
        assertThrows(ResponseStatusException.class, () -> IsbnUtil.normalizar("9780132350885"));
        assertThrows(ResponseStatusException.class, () -> IsbnUtil.normalizar("abc"));
        assertThrows(ResponseStatusException.class, () -> IsbnUtil.normalizar(null));
    }

    @Test
    void googleBooksInterpretaResposta() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://g.test/volumes?q=isbn:" + ISBN))
                .andRespond(withSuccess("""
                        {"items":[{"volumeInfo":{"title":"Clean Code","subtitle":"A Handbook",
                        "authors":["Robert C. Martin"],"publisher":"Prentice Hall","publishedDate":"2008-08-01",
                        "pageCount":464,"categories":["Computers"],"imageLinks":{"thumbnail":"http://x/c.jpg"}}}]}
                        """, MediaType.APPLICATION_JSON));

        LivroExterno livro = new GoogleBooksFonte(builder.build(), "https://g.test/volumes")
                .buscar(ISBN, ChavesIsbn.VAZIAS).orElseThrow();

        assertEquals("Clean Code", livro.titulo());
        assertEquals(List.of("Robert C. Martin"), livro.autores());
        assertEquals("Prentice Hall", livro.editora());
        assertEquals(2008, livro.anoPublicacao());
        assertEquals(464, livro.quantidadePaginas());
        assertEquals(List.of("Computers"), livro.generos());
        server.verify();
    }

    @Test
    void googleBooksUsaChaveSomenteSeInformada() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://g.test/volumes?q=isbn:" + ISBN + "&key=K1"))
                .andRespond(withSuccess("{\"totalItems\":0}", MediaType.APPLICATION_JSON));

        Optional<LivroExterno> r = new GoogleBooksFonte(builder.build(), "https://g.test/volumes")
                .buscar(ISBN, new ChavesIsbn("K1", null, null, null));

        assertTrue(r.isEmpty());
        server.verify();
    }

    @Test
    void openLibraryInterpretaResposta() {
        OpenLibraryFonte fonte = new OpenLibraryFonte(RestClient.create(), "http://x");
        LivroExterno livro = fonte.interpretar("""
                {"ISBN:%s":{"title":"Clean Code","authors":[{"name":"Robert C. Martin"}],
                "publishers":[{"name":"Prentice Hall"}],"publish_date":"August 2008","number_of_pages":464,
                "subjects":[{"name":"Software"}]}}
                """.formatted(ISBN), ISBN).orElseThrow();

        assertEquals("Prentice Hall", livro.editora());
        assertEquals(2008, livro.anoPublicacao());
        assertEquals(List.of("Robert C. Martin"), livro.autores());
        assertTrue(fonte.interpretar("{}", ISBN).isEmpty());
    }

    @Test
    void brasilApiInterpretaResposta() {
        LivroExterno livro = new BrasilApiFonte(RestClient.create(), "http://x").interpretar("""
                {"title":"Dom Casmurro","authors":["Machado de Assis"],"publisher":"Editora X","year":2010,
                "page_count":200,"synopsis":"Resumo","subjects":["Romance"]}
                """, ISBN).orElseThrow();

        assertEquals("Dom Casmurro", livro.titulo());
        assertEquals(2010, livro.anoPublicacao());
        assertEquals("Resumo", livro.sinopse());
    }

    @Test
    void isbnDbEnviaChaveNoHeaderEFicaIndisponivelSemChave() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://i.test/book/" + ISBN)).andExpect(header("Authorization", "KEY"))
                .andRespond(withSuccess("""
                        {"book":{"title":"Clean Code","publisher":"PH","authors":["R. Martin"],
                        "date_published":"2008","pages":464,"subjects":["Tech"]}}
                        """, MediaType.APPLICATION_JSON));
        IsbnDbFonte fonte = new IsbnDbFonte(builder.build(), "https://i.test/book");

        assertFalse(fonte.disponivel(ChavesIsbn.VAZIAS));
        assertTrue(fonte.disponivel(new ChavesIsbn(null, "KEY", null, null)));
        assertEquals("Clean Code", fonte.buscar(ISBN, new ChavesIsbn(null, "KEY", null, null)).orElseThrow().titulo());
        server.verify();
    }

    @Test
    void worldCatObtemTokenEBusca() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://o.test/token"))
                .andRespond(withSuccess("{\"access_token\":\"TOK\"}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://w.test/bibs?q=bn:" + ISBN)).andExpect(header("Authorization", "Bearer TOK"))
                .andRespond(withSuccess("""
                        {"bibRecords":[{"title":{"mainTitles":[{"text":"Clean Code"}]},
                        "contributor":{"creators":[{"firstName":{"text":"Robert"},"secondName":{"text":"Martin"}}]},
                        "publishing":{"publisherName":"PH"},"date":{"publicationDate":"2008"}}]}
                        """, MediaType.APPLICATION_JSON));
        WorldCatFonte fonte = new WorldCatFonte(builder.build(), "https://o.test/token", "https://w.test/bibs");

        assertFalse(fonte.disponivel(new ChavesIsbn(null, null, "K", null)));
        LivroExterno livro = fonte.buscar(ISBN, new ChavesIsbn(null, null, "K", "S")).orElseThrow();

        assertEquals("Clean Code", livro.titulo());
        assertEquals(List.of("Robert Martin"), livro.autores());
        assertEquals("PH", livro.editora());
        server.verify();
    }

    @Test
    void fonteComErroHttpLancaExcecao() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://b.test/" + ISBN)).andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThrows(RuntimeException.class,
                () -> new BrasilApiFonte(builder.build(), "http://b.test").buscar(ISBN, ChavesIsbn.VAZIAS));
    }

    private static FonteIsbn fonte(String nome, boolean disponivel, List<String> chamadas,
            Supplier<Optional<LivroExterno>> resultado) {
        return new FonteIsbn() {
            public String nome() {
                return nome;
            }

            public boolean disponivel(ChavesIsbn c) {
                return disponivel;
            }

            public Optional<LivroExterno> buscar(String isbn, ChavesIsbn c) {
                chamadas.add(nome);
                return resultado.get();
            }
        };
    }

    private static Optional<LivroExterno> livro(String fonte) {
        return Optional.of(new LivroExterno(ISBN, "T", null, null, null, null, null, null, null, null, null, fonte));
    }

    private static ConfiguracaoRepository repoSemChaves() {
        ConfiguracaoRepository repo = mock(ConfiguracaoRepository.class);
        when(repo.findByFilialIdAndChave(anyLong(), anyString())).thenReturn(Optional.empty());
        return repo;
    }

    @Test
    void orquestradorSegueParaProximaNaFalhaVaziaEPulaSemChave() {
        List<String> chamadas = new ArrayList<>();
        BuscaIsbnService servico = new BuscaIsbnService(List.of(
                fonte("A", true, chamadas, () -> {
                    throw new IllegalStateException("timeout");
                }),
                fonte("B", true, chamadas, Optional::empty),
                fonte("C", false, chamadas, () -> livro("C")),
                fonte("D", true, chamadas, () -> livro("D")),
                fonte("E", true, chamadas, () -> livro("E"))), repoSemChaves());

        LivroExterno r = servico.buscar(ISBN, 1L).orElseThrow();

        assertEquals("D", r.fonte());
        assertEquals(List.of("A", "B", "D"), chamadas);
    }

    @Test
    void orquestradorRetornaVazioQuandoNinguemAchaEUsaCache() {
        List<String> chamadas = new ArrayList<>();
        BuscaIsbnService vazio = new BuscaIsbnService(List.of(fonte("A", true, chamadas, Optional::empty)),
                repoSemChaves());
        assertTrue(vazio.buscar(ISBN, 1L).isEmpty());

        BuscaIsbnService comCache = new BuscaIsbnService(List.of(fonte("B", true, chamadas, () -> livro("B"))),
                repoSemChaves());
        comCache.buscar(ISBN, 1L);
        comCache.buscar(ISBN, 1L);
        assertEquals(List.of("A", "B"), chamadas);
    }
}
