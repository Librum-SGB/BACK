package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HistoricoMultaRequestDTO {
    @NotNull(message = "O empréstimo é obrigatório")
    private Long emprestimoId;
    
    @NotNull(message = "O valor é obrigatório")
    private BigDecimal valor;
    
    @NotNull(message = "Os dias de atraso são obrigatórios")
    private Integer diasAtraso;
    
    private Boolean pago;
    private LocalDate dataPagamento;
}