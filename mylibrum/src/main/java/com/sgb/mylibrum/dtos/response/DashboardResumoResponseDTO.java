package com.sgb.mylibrum.dtos.response;

import java.util.List;

import lombok.Data;

@Data
public class DashboardResumoResponseDTO {
    private long alunosCadastrados;
    private long livrosNoAcervo;
    private long emprestimosAtivos;
    private long emprestimosAVencer;
    private long emAndamento;
    private long vencidos;
    private List<EmprestimoResponseDTO> emprestimos;
}
