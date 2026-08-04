package com.sgb.mylibrum.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprestimoDTO {
    private Long id;
    private Long usuarioId;
    private Long exemplarId;
    private Long gestorId;
    private OffsetDateTime dataSaida;
    private LocalDate dataDevolucaoPrevista;
    private OffsetDateTime dataDevolucaoEfetivada;
    private Integer renovacoesContagem;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}