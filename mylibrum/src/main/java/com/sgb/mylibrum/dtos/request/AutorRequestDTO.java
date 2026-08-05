package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AutorRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    private String nacionalidade;
    private LocalDate dataNascimento;
    private LocalDate dataFalecimento;
    private String biografia;
}