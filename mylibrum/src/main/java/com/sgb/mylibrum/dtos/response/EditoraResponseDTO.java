package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Editora do acervo, com identificador e datas de auditoria.")
public class EditoraResponseDTO {
    private Long id;
    private String nome;
    private String nacionalidade;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}