package com.sgb.mylibrum.dtos.response;

import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Empréstimo e seu estado atual, prazos, responsável, contagem de renovações e datas de auditoria.")
public class EmprestimoResponseDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private Long exemplarId;
    private String materialTitulo;
    private Long usuarioResponsavelId;
    private StatusEmprestimo status;
    private OffsetDateTime dataSaida;
    private LocalDate dataDevolucaoPrevista;
    private OffsetDateTime dataDevolucaoEfetivada;
    private Integer renovacoesContagem;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}