package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Configuração cadastrada para uma filial, com identificador e datas de auditoria.")
public class ConfiguracaoResponseDTO {
    private Long id;
    private Long filialId;
    private String chave;
    private String valor;
    private String descricao;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}