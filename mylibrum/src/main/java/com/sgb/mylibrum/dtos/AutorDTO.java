package com.sgb.mylibrum.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutorDTO {
    private Long id;
    private String nome;
    private String nacionalidade;
    private LocalDate dataNascimento;
    private LocalDate dataFalecimento;
    private String biografia;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}