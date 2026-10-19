package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
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