package com.sgb.mylibrum.dtos.request;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ListaTarefaRequestDTO {
    @NotNull(message = "O gestor é obrigatório")
    private Long gestorId;
    
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    
    private PrioridadeTarefa prioridade;
    private Boolean concluida;
}