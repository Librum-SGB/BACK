package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.StatusExemplar;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class ExemplarResponseDTO {
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