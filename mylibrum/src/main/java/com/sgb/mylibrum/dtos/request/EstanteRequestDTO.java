package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EstanteRequestDTO {
    @NotBlank(message = "A localização é obrigatória")
    private String localizacao;
    
    private Integer capacidade;
}