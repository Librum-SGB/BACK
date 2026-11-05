package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Dados para criar ou atualizar um empréstimo, incluindo usuário, exemplar, responsável e prazo de devolução.")
public class EmprestimoRequestDTO {
    @NotNull(message = "O usuário é obrigatório")
    private Long usuarioId;
    
    @NotNull(message = "O exemplar é obrigatório")
    private Long exemplarId;
    
    @NotNull(message = "O usuário responsável é obrigatório")
    private Long usuarioResponsavelId;
    
    @NotNull(message = "A data de devolução prevista é obrigatória")
    private LocalDate dataDevolucaoPrevista;
    
    private OffsetDateTime dataSaida;
    private OffsetDateTime dataDevolucaoEfetivada;
    private Integer renovacoesContagem;
}