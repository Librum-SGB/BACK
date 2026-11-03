package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.StatusExemplar;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Exemplar físico de um material, com código de barras, localização, estado e datas de auditoria.")
public class ExemplarResponseDTO {
    private Long id;
    private Long materialId;
    private Long filialId;
    private Long estanteId;
    private String prateleira;
    private String posicao;
    private String codigoBarras;
    private StatusExemplar status;
    private LocalDate dataAquisicao;
    private String observacoes;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}