package com.sgb.mylibrum.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class EntidadeAuditavelTest {

    @Test
    void deveInicializarEntidadeComoAtivaENaoExcluida() {
        Autor autor = new Autor();

        assertTrue(autor.getAtivo());
        assertFalse(autor.getExcluido());
    }

    @Test
    void devePreencherDatasEmUtcAoCriar() {
        Autor autor = new Autor();

        autor.aoCriar();

        OffsetDateTime dataCriacao = autor.getDataCriacao();
        OffsetDateTime dataAtualizacaoNaCriacao = autor.getDataUltimaAtualizacao();

        assertNotNull(dataCriacao);
        assertNotNull(dataAtualizacaoNaCriacao);
        assertEquals(dataCriacao, dataAtualizacaoNaCriacao);
        assertEquals(ZoneOffset.UTC, dataCriacao.getOffset());
        assertEquals(ZoneOffset.UTC, dataAtualizacaoNaCriacao.getOffset());
    }

    @Test
    void devePreservarDataDeCriacaoExistenteAoPersistirNovamente() {
        Autor autor = new Autor();
        OffsetDateTime dataCriacaoOriginal = OffsetDateTime.parse("2024-01-02T03:04:05+03:00");
        autor.setDataCriacao(dataCriacaoOriginal);

        autor.aoCriar();

        assertEquals(dataCriacaoOriginal, autor.getDataCriacao());
        assertNotNull(autor.getDataUltimaAtualizacao());
        assertEquals(ZoneOffset.UTC, autor.getDataUltimaAtualizacao().getOffset());
    }

    @Test
    void deveAtualizarSomenteDataDeAtualizacaoAoAlterar() {
        Autor autor = new Autor();
        OffsetDateTime dataCriacao = OffsetDateTime.parse("2024-01-02T03:04:05Z");
        OffsetDateTime dataAtualizacaoAnterior = OffsetDateTime.parse("2000-02-02T03:04:05Z");
        autor.setDataCriacao(dataCriacao);
        autor.setDataUltimaAtualizacao(dataAtualizacaoAnterior);

        autor.aoAtualizar();

        assertEquals(dataCriacao, autor.getDataCriacao());
        assertNotNull(autor.getDataUltimaAtualizacao());
        assertEquals(ZoneOffset.UTC, autor.getDataUltimaAtualizacao().getOffset());
        assertTrue(!autor.getDataUltimaAtualizacao().isBefore(dataAtualizacaoAnterior));
    }

    @Test
    void naoDeveDefinirDataDeCriacaoDuranteAtualizacao() {
        Autor autor = new Autor();

        autor.aoAtualizar();

        assertNull(autor.getDataCriacao());
        assertNotNull(autor.getDataUltimaAtualizacao());
        assertEquals(ZoneOffset.UTC, autor.getDataUltimaAtualizacao().getOffset());
    }
}
