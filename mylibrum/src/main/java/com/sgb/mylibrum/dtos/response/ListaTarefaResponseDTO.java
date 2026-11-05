package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Tarefa associada a um usuário, com prioridade, estado, prazo e datas de auditoria.")
public class ListaTarefaResponseDTO {
    private Long id;
    private Long usuarioId;
    private String descricao;
    private PrioridadeTarefa prioridade;
    private StatusTarefa status;
    private LocalDate prazo;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}