package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Token de recuperação e nova senha, que deve ter de 6 a 72 caracteres.")
public class RedefinirSenhaRequestDTO {
    @NotBlank(message = "O token é obrigatório")
    private String token;

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(min = 6, max = 72, message = "A nova senha deve ter entre 6 e 72 caracteres")
    private String novaSenha;
}
