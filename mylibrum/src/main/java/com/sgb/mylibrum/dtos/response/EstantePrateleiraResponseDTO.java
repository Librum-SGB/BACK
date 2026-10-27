package com.sgb.mylibrum.dtos.response;

import lombok.Data;

@Data
public class EstantePrateleiraResponseDTO {
    private Long id;
    private String nome;
    private Integer posicaoInicial;
    private Integer posicaoFinal;
    private Integer ordem;
}
