package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "Dados para cadastrar ou atualizar um autor. O nome é obrigatório.")
public class AutorRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    private String nacionalidade;
    private LocalDate dataNascimento;
    private LocalDate dataFalecimento;
    private String biografia;
}