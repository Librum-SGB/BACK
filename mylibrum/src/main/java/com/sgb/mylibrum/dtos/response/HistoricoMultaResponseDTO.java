package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class HistoricoMultaResponseDTO {
    private Long id;
    private Long emprestimoId;
    private BigDecimal valor;
    private Integer diasAtraso;
    private Boolean pago;
    private LocalDate dataPagamento;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}