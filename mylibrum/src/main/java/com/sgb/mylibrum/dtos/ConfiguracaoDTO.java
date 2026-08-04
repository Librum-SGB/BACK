package com.sgb.mylibrum.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoDTO {
    private Long id;
    private Long filialId;
    private String chave;
    private String valor;
    private String descricao;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}