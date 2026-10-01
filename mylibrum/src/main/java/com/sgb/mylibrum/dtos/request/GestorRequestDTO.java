package com.sgb.mylibrum.dtos.request;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GestorRequestDTO {
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;
    
    @NotBlank(message = "A senha é obrigatória")
    private String senha;
    
    @NotBlank(message = "A matrícula do funcionário é obrigatória")
    private String matriculaFuncionario;
    
    @NotNull(message = "A filial é obrigatória")
    private Long filialId;
    
    private OffsetDateTime ultimoAcesso;
}