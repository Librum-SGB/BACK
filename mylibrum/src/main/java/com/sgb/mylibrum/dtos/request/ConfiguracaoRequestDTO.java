package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados de uma configuração vinculada a uma filial. Filial, chave e valor são obrigatórios.")
public class ConfiguracaoRequestDTO {
    @NotNull(message = "A filial é obrigatória")
    private Long filialId;
    
    @NotBlank(message = "A chave é obrigatória")
    private String chave;
    
    @NotBlank(message = "O valor é obrigatório")
    private String valor;
    
    private String descricao;
}