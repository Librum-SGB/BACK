package com.sgb.mylibrum.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;

public final class ExemplarSpecification {

    private ExemplarSpecification() {
    }

    public static Specification<Exemplar> filtrar(ExemplarFiltro filtro) {
        FiltrosUtil.validarIntervalo(filtro.dataAquisicaoDe(), filtro.dataAquisicaoAte(), "dataAquisicao");
        return AtivoSpecification.<Exemplar>ativo(filtro.ativo())
                .and(AtivoSpecification.<Exemplar>igual("status", filtro.status()))
                .and(AtivoSpecification.<Exemplar>igual("material.id", filtro.materialId()))
                .and(AtivoSpecification.<Exemplar>igual("filial.id", filtro.filialId()))
                .and(AtivoSpecification.<Exemplar>igual("estante.id", filtro.estanteId()))
                .and(AtivoSpecification.contemIgnoreCase("codigoBarras", filtro.codigoBarras()))
                .and(FiltrosUtil.<Exemplar, LocalDate>entre(
                        "dataAquisicao", filtro.dataAquisicaoDe(), filtro.dataAquisicaoAte()));
    }
}
