package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class EstanteResponseDTO {
    private Long id;
    private Long filialId;
    private String nome;
    private String localizacao;
    private Integer capacidade;
    private Integer qtdPrateleiras;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}