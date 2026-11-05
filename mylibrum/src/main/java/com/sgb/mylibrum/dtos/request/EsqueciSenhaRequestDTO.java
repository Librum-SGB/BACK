package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "E-mail para o qual serão enviadas instruções de recuperação, se houver uma conta correspondente.")
public class EsqueciSenhaRequestDTO {
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;
}
