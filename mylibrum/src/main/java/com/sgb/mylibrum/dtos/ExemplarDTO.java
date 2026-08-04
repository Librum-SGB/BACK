package com.sgb.mylibrum.dtos;

import com.sgb.mylibrum.entities.enums.StatusExemplar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExemplarDTO {
    private Long id;
    private Long livroId;
    private Long filialId;
    private Long estanteId;
    private String prateleira;
    private String posicao;
    private String codigoBarras;
    private StatusExemplar status;
    private LocalDate dataAquisicao;
    private String observacoes;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}