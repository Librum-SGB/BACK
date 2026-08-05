package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class EmprestimoRequestDTO {
    @NotNull(message = "O usuário é obrigatório")
    private Long usuarioId;
    
    @NotNull(message = "O exemplar é obrigatório")
    private Long exemplarId;
    
    @NotNull(message = "O gestor é obrigatório")
    private Long gestorId;
    
    @NotNull(message = "A data de devolução prevista é obrigatória")
    private LocalDate dataDevolucaoPrevista;
    
    private OffsetDateTime dataSaida;
    private OffsetDateTime dataDevolucaoEfetivada;
    private Integer renovacoesContagem;
}