package com.sgb.mylibrum.dtos.request;

import com.sgb.mylibrum.entities.enums.StatusExemplar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ExemplarRequestDTO {
    @NotNull(message = "O material é obrigatório")
    private Long materialId;
    
    @NotNull(message = "A filial é obrigatória")
    private Long filialId;
    
    @NotNull(message = "A estante é obrigatória")
    private Long estanteId;
    
    @NotBlank(message = "O código de barras é obrigatório")
    private String codigoBarras;
    
    private String prateleira;
    private String posicao;
    private StatusExemplar status;
    private LocalDate dataAquisicao;
    private String observacoes;
}