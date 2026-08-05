package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class GeneroResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}