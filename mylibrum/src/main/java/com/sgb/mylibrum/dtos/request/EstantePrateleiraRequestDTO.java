package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class EstantePrateleiraRequestDTO {
    private Long id;

    @NotBlank
    private String nome;

    @NotNull
    @Positive
    private Integer posicaoInicial;

    @NotNull
    @Positive
    private Integer posicaoFinal;

    private Integer ordem;
}
