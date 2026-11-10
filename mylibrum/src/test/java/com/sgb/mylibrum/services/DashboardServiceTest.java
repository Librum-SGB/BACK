package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.sgb.mylibrum.dtos.mapper.EmprestimoMapper;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MaterialRepository materialRepository;
    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private EmprestimoMapper emprestimoMapper;

    @Test
    void resumoContaAlunosLivrosEClassificaEmprestimosPeloPrazo() {
        LocalDate hoje = LocalDate.now();
        List<Emprestimo> emprestimos = List.of(
                emprestimo(1L, StatusEmprestimo.ATIVO, hoje.plusDays(8)),
                emprestimo(2L, StatusEmprestimo.ATIVO, hoje.plusDays(7)),
                emprestimo(3L, StatusEmprestimo.ATIVO, hoje),
                emprestimo(4L, StatusEmprestimo.ATRASADO, hoje.minusDays(1)));
        when(usuarioRepository.count(any(Specification.class))).thenReturn(11L);
        when(materialRepository.count(any(Specification.class))).thenReturn(6L);
        when(emprestimoRepository.countByAtivoTrueAndStatusIn(anyList())).thenReturn(4L);
        when(emprestimoRepository.countByAtivoTrueAndStatusInAndDataDevolucaoPrevistaBetween(
                anyList(), eq(hoje), eq(hoje.plusDays(7)))).thenReturn(2L);
        when(emprestimoRepository.countByAtivoTrueAndStatusInAndDataDevolucaoPrevistaBefore(
                anyList(), eq(hoje))).thenReturn(1L);
        when(emprestimoRepository.findTop10ByAtivoTrueAndStatusInOrderByDataDevolucaoPrevistaAsc(anyList()))
                .thenReturn(emprestimos);
        when(emprestimoMapper.toResponseDTO(any(Emprestimo.class))).thenAnswer(invocation -> {
            EmprestimoResponseDTO dto = new EmprestimoResponseDTO();
            dto.setId(((Emprestimo) invocation.getArgument(0)).getId());
            return dto;
        });

        var resumo = new DashboardService(
                usuarioRepository, materialRepository, emprestimoRepository, emprestimoMapper).obterResumo();

        assertEquals(11, resumo.getAlunosCadastrados());
        assertEquals(6, resumo.getLivrosNoAcervo());
        assertEquals(4, resumo.getEmprestimosAtivos());
        assertEquals(2, resumo.getEmprestimosAVencer());
        assertEquals(1, resumo.getEmAndamento());
        assertEquals(1, resumo.getVencidos());
        assertEquals(4, resumo.getEmprestimos().size());
    }

    private Emprestimo emprestimo(Long id, StatusEmprestimo status, LocalDate devolucaoPrevista) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(id);
        emprestimo.setStatus(status);
        emprestimo.setDataDevolucaoPrevista(devolucaoPrevista);
        return emprestimo;
    }
}
