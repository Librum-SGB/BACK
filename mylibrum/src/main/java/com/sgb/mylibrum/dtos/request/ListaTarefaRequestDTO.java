package com.sgb.mylibrum.dtos.request;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Dados de uma tarefa. No endpoint de criação, o usuário associado é definido pela autenticação.")
public class ListaTarefaRequestDTO {
    private Long usuarioId;
    
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    
    private PrioridadeTarefa prioridade;
    @NotNull(message = "O status é obrigatório")
    private StatusTarefa status = StatusTarefa.PENDENTE;
    private LocalDate prazo;
}