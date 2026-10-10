package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstanteRequestDTO {
    @NotNull(message = "A filial é obrigatória")
    private Long filialId;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A localização é obrigatória")
    private String localizacao;
    
    private Integer capacidade;
    private Integer qtdPrateleiras;
}