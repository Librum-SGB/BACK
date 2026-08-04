package com.sgb.mylibrum.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMultaDTO {
    private Long id;
    private Long emprestimoId;
    private BigDecimal valor;
    private Integer diasAtraso;
    private Boolean pago;
    private LocalDate dataPagamento;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}