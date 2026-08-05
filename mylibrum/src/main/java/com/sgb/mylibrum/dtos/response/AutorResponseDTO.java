package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
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