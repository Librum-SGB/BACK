package com.sgb.mylibrum.entities;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class EntidadeAuditavelTest {

    @Test
    void devePreencherDatasAoCriarEAtualizar() {
        Autor autor = new Autor();

        autor.aoCriar();

        OffsetDateTime dataCriacao = autor.getDataCriacao();
        OffsetDateTime dataAtualizacaoNaCriacao = autor.getDataUltimaAtualizacao();

        assertNotNull(dataCriacao);
        assertNotNull(dataAtualizacaoNaCriacao);
        assertEquals(dataCriacao, dataAtualizacaoNaCriacao);

        autor.aoAtualizar();

        assertEquals(dataCriacao, autor.getDataCriacao());
        assertNotNull(autor.getDataUltimaAtualizacao());
        assertTrue(!autor.getDataUltimaAtualizacao().isBefore(dataAtualizacaoNaCriacao));
    }
}
