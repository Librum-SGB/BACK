package com.sgb.mylibrum.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgb.mylibrum.dtos.mapper.EmprestimoMapper;
import com.sgb.mylibrum.dtos.response.DashboardResumoResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.entities.enums.TipoMaterial;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int DIAS_PARA_VENCER = 7;
    private static final List<StatusEmprestimo> STATUS_EM_ABERTO =
            List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO);

    private final UsuarioRepository usuarioRepository;
    private final MaterialRepository materialRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoMapper emprestimoMapper;

    @Transactional(readOnly = true)
    public DashboardResumoResponseDTO obterResumo() {
        Specification<Usuario> alunosAtivos = AtivoSpecification.<Usuario>ativo(true)
                .and(AtivoSpecification.igual("funcao", FuncaoUsuario.USUARIO));
        Specification<Material> livrosAtivos =
                AtivoSpecification.<Material>ativo(true)
                        .and(AtivoSpecification.igual("tipo", TipoMaterial.LIVRO));

        LocalDate hoje = LocalDate.now();
        LocalDate limiteVencimento = hoje.plusDays(DIAS_PARA_VENCER);
        long emprestimosAtivos = emprestimoRepository.countByAtivoTrueAndStatusIn(STATUS_EM_ABERTO);
        long aVencer = emprestimoRepository.countByAtivoTrueAndStatusInAndDataDevolucaoPrevistaBetween(
                STATUS_EM_ABERTO, hoje, limiteVencimento);
        long vencidos = emprestimoRepository.countByAtivoTrueAndStatusInAndDataDevolucaoPrevistaBefore(
                STATUS_EM_ABERTO, hoje);
        long emAndamento = emprestimosAtivos - vencidos - aVencer;
        List<Emprestimo> emprestimosRecentes =
                emprestimoRepository.findTop10ByAtivoTrueAndStatusInOrderByDataDevolucaoPrevistaAsc(STATUS_EM_ABERTO);

        DashboardResumoResponseDTO resumo = new DashboardResumoResponseDTO();
        resumo.setAlunosCadastrados(usuarioRepository.count(alunosAtivos));
        resumo.setLivrosNoAcervo(materialRepository.count(livrosAtivos));
        resumo.setEmprestimosAtivos(emprestimosAtivos);
        resumo.setEmprestimosAVencer(aVencer);
        resumo.setEmAndamento(emAndamento);
        resumo.setVencidos(vencidos);
        resumo.setEmprestimos(emprestimosRecentes.stream()
                .map(emprestimoMapper::toResponseDTO)
                .toList());
        return resumo;
    }
}
