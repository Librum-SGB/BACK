package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.StatusMulta;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class HistoricoMultaResponseDTO {
    private Long id;
    private Long emprestimoId;
    private Long usuarioId;
    private BigDecimal valor;
    private Integer diasAtraso;
    private StatusMulta status;
    private LocalDate dataPagamento;
    private OffsetDateTime dataGeracao;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}