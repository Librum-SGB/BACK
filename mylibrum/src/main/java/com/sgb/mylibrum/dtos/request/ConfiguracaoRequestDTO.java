package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfiguracaoRequestDTO {
    @NotNull(message = "A filial é obrigatória")
    private Long filialId;
    
    @NotBlank(message = "A chave é obrigatória")
    private String chave;
    
    @NotBlank(message = "O valor é obrigatório")
    private String valor;
    
    private String descricao;
}