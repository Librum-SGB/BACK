package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class EditoraResponseDTO {
    private Long id;
    private String nome;
    private String nacionalidade;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}