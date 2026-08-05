package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class EstanteResponseDTO {
    private Long id;
    private String localizacao;
    private Integer capacidade;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}