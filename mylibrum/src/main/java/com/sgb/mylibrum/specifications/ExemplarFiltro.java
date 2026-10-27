package com.sgb.mylibrum.specifications;

import java.time.LocalDate;

import com.sgb.mylibrum.entities.enums.StatusExemplar;

public record ExemplarFiltro(
        StatusExemplar status,
        Long materialId,
        Long filialId,
        Long estanteId,
        String codigoBarras,
        LocalDate dataAquisicaoDe,
        LocalDate dataAquisicaoAte,
        Boolean ativo) {
}
