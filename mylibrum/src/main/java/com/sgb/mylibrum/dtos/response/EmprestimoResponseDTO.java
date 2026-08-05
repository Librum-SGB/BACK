package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class EmprestimoResponseDTO {
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