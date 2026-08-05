package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class ListaTarefaResponseDTO {
    private Long id;
    private Long gestorId;
    private String descricao;
    private PrioridadeTarefa prioridade;
    private Boolean concluida;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}