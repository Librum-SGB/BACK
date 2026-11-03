package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Gênero literário cadastrado, com identificador e datas de auditoria.")
public class GeneroResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}