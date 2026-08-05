package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GeneroRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    
    private String descricao;
}