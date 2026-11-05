package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados para cadastrar ou atualizar uma editora. O nome é obrigatório.")
public class EditoraRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    
    private String nacionalidade;
}