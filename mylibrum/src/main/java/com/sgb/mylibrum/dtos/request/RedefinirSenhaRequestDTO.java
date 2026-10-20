package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RedefinirSenhaRequestDTO {
    @NotBlank(message = "O token é obrigatório")
    private String token;

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(min = 6, max = 72, message = "A nova senha deve ter entre 6 e 72 caracteres")
    private String novaSenha;
}
