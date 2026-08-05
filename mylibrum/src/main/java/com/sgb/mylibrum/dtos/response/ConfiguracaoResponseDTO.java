package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class ConfiguracaoResponseDTO {
    private Long id;
    private Long filialId;
    private String chave;
    private String valor;
    private String descricao;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}