package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class GestorRequestDTO {
    @NotBlank(message = "O login é obrigatório")
    private String login;
    
    @NotBlank(message = "A senha é obrigatória")
    private String senha;
    
    @NotBlank(message = "A matrícula do funcionário é obrigatória")
    private String matriculaFuncionario;
    
    @NotNull(message = "A filial é obrigatória")
    private Long filialId;
    
    private OffsetDateTime ultimoAcesso;
}