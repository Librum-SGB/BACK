package com.sgb.mylibrum.specifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class FiltrosUtilTest {

    @Test
    void deveConsiderarAtivosPorPadrao() {
        assertEquals(Boolean.TRUE, FiltrosUtil.interpretarAtivo(null));
        assertEquals(Boolean.TRUE, FiltrosUtil.interpretarAtivo("  "));
    }

    @Test
    void deveInterpretarFiltroAtivoSemDiferenciarMaiusculas() {
        assertEquals(Boolean.TRUE, FiltrosUtil.interpretarAtivo(" TRUE "));
        assertEquals(Boolean.FALSE, FiltrosUtil.interpretarAtivo("False"));
        assertNull(FiltrosUtil.interpretarAtivo(" TODOS "));
    }

    @Test
    void deveRejeitarValorAtivoDesconhecidoComErro400() {
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> FiltrosUtil.interpretarAtivo("yes"));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
    }

    @Test
    void deveNormalizarTextoVazioParaNuloERemoverEspacosExternos() {
        assertNull(FiltrosUtil.textoOuNulo(null));
        assertNull(FiltrosUtil.textoOuNulo("  "));
        assertEquals("titulo", FiltrosUtil.textoOuNulo("  titulo  "));
    }

    @Test
    void deveRejeitarIntervaloInvertido() {
        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> FiltrosUtil.validarIntervalo(2025, 2024, "anoPublicacao"));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
    }

    @Test
    void deveAceitarIntervaloAbertoOuOrdenado() {
        FiltrosUtil.validarIntervalo(2024, null, "anoPublicacao");
        FiltrosUtil.validarIntervalo(null, 2024, "anoPublicacao");
        FiltrosUtil.validarIntervalo(2024, 2024, "anoPublicacao");
        FiltrosUtil.validarIntervalo(2023, 2024, "anoPublicacao");
    }
}
