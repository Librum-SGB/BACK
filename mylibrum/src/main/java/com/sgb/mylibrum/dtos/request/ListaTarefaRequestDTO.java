package com.sgb.mylibrum.dtos.request;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ListaTarefaRequestDTO {
    @NotNull(message = "O usuário é obrigatório")
    private Long usuarioId;
    
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    
    private PrioridadeTarefa prioridade;
    @NotNull(message = "O status é obrigatório")
    private StatusTarefa status = StatusTarefa.PENDENTE;
    private LocalDate prazo;
}