package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.StatusMulta;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Multa associada a um empréstimo, com situação de pagamento, valor, atraso e datas.")
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