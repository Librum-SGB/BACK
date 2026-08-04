package com.sgb.mylibrum.dtos;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaTarefaDTO {
    private Long id;
    private Long gestorId;
    private String descricao;
    private PrioridadeTarefa prioridade;
    private Boolean concluida;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}