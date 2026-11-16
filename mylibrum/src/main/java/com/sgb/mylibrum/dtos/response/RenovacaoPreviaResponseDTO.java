package com.sgb.mylibrum.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Simulação da renovação de um empréstimo, sem alterá-lo: indica se é permitida, a nova data de devolução e as renovações restantes.")
public class RenovacaoPreviaResponseDTO {
    @Schema(description = "Identificador do empréstimo.")
    private Long emprestimoId;
    @Schema(description = "Indica se o empréstimo pode ser renovado agora.")
    private boolean permitida;
    @Schema(description = "Motivo pelo qual a renovação não é permitida; ausente quando permitida.")
    private String motivo;
    @Schema(description = "Data de devolução prevista atual.")
    private LocalDate dataDevolucaoAtual;
    @Schema(description = "Data de devolução após a renovação; ausente quando não permitida.")
    private LocalDate novaDataDevolucao;
    @Schema(description = "Quantidade de renovações já realizadas.")
    private Integer renovacoesRealizadas;
    @Schema(description = "Limite de renovações configurado para a filial; ausente se não configurado.")
    private Integer limiteRenovacoes;
    @Schema(description = "Renovações que ainda restam depois desta, quando permitida; senão, as restantes agora.")
    private Integer renovacoesRestantes;
}
