package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Dados de uma multa associada a um empréstimo, incluindo valor e quantidade de dias de atraso.")
public class HistoricoMultaRequestDTO {
    @NotNull(message = "O empréstimo é obrigatório")
    private Long emprestimoId;
    
    @NotNull(message = "O valor é obrigatório")
    private BigDecimal valor;
    
    @NotNull(message = "Os dias de atraso são obrigatórios")
    private Integer diasAtraso;
    
}