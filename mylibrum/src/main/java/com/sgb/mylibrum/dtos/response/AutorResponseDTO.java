package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Representação de um autor, incluindo dados biográficos e datas de auditoria.")
public class AutorResponseDTO {
    private Long id;
    private String nome;
    private String nacionalidade;
    private LocalDate dataNascimento;
    private LocalDate dataFalecimento;
    private String biografia;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}