package com.sgb.mylibrum.dtos.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmprestimoLoteRequestDTO {

    @NotNull(message = "O usuário é obrigatório")
    private Long usuarioId;

    @NotEmpty(message = "Selecione ao menos um exemplar")
    private List<@NotNull(message = "O exemplar é obrigatório") Long> exemplarIds;

    @NotNull(message = "A data de devolução prevista é obrigatória")
    @FutureOrPresent(message = "A data de devolução não pode estar no passado")
    private LocalDate dataDevolucaoPrevista;
}
