package com.sgb.mylibrum.services;

import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.dtos.mapper.HistoricoMultaMapper;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.HistoricoMulta;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.HistoricoMultaRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoMultaServiceTest {

    @Mock
    private HistoricoMultaRepository repository;
    @Mock
    private EmprestimoRepository emprestimoRepository;
    @Mock
    private ConfiguracaoRepository configuracaoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    private HistoricoMultaService service;

    @BeforeEach
    void setUp() {
        service = new HistoricoMultaService(
                repository, emprestimoRepository, configuracaoRepository, usuarioRepository,
                new HistoricoMultaMapper());
    }

    @Test
    void deveGerarMultaPelosDiasDeAtrasoEBloquearUsuario() {
        Usuario usuario = usuario();
        Emprestimo emprestimo = emprestimo(usuario);
        Configuracao configuracao = new Configuracao();
        configuracao.setValor("2.50");
        when(emprestimoRepository.findByStatusInAndDataDevolucaoPrevistaBefore(
                any(), any(LocalDate.class))).thenReturn(List.of(emprestimo));
        when(configuracaoRepository.findByFilialIdAndChave(5L, "multa.valor.dia"))
                .thenReturn(Optional.of(configuracao));
        when(repository.findByEmprestimoId(10L)).thenReturn(List.of());
        when(repository.save(any(HistoricoMulta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.existsByUsuarioIdAndStatus(1L, StatusMulta.PENDENTE)).thenReturn(true);

        service.gerarMultasPorAtraso();

        ArgumentCaptor<HistoricoMulta> multaCaptor = ArgumentCaptor.forClass(HistoricoMulta.class);
        verify(repository).save(multaCaptor.capture());
        assertEquals(2, multaCaptor.getValue().getDiasAtraso());
        assertEquals(new BigDecimal("5.00"), multaCaptor.getValue().getValor());
        assertEquals(StatusMulta.PENDENTE, multaCaptor.getValue().getStatus());
        assertEquals(StatusEmprestimo.ATRASADO, emprestimo.getStatus());
        assertEquals(true, usuario.getEstaBloqueado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveRegistrarPagamentoEDesbloquearSemMultasPendentes() {
        Usuario usuario = usuario();
        usuario.setEstaBloqueado(true);
        HistoricoMulta multa = new HistoricoMulta();
        multa.setId(20L);
        multa.setUsuario(usuario);
        multa.setStatus(StatusMulta.PENDENTE);
        when(repository.findByIdForUpdate(20L)).thenReturn(Optional.of(multa));
        when(repository.save(multa)).thenReturn(multa);
        when(repository.existsByUsuarioIdAndStatus(1L, StatusMulta.PENDENTE)).thenReturn(false);

        var resposta = service.registrarPagamento(20L);

        assertEquals(StatusMulta.PAGA, resposta.getStatus());
        assertNotNull(resposta.getDataPagamento());
        assertEquals(false, usuario.getEstaBloqueado());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveFiltrarMultasPorStatus() {
        when(repository.findAll(any(Specification.class))).thenReturn(List.of());

        var resultado = service.findAll(StatusMulta.PENDENTE, null);

        assertEquals(List.of(), resultado);
        verify(repository).findAll(any(Specification.class));
    }

    @Test
    void deveBuscarMultasPorUsuario() {
        when(repository.findAll(any(Specification.class))).thenReturn(List.of());

        var resultado = service.findByUsuarioId(1L, null);

        assertEquals(List.of(), resultado);
        verify(repository).findAll(any(Specification.class));
    }

    private Usuario usuario() {
        Filial filial = new Filial();
        filial.setId(5L);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setFilial(filial);
        usuario.setEstaBloqueado(false);
        return usuario;
    }

    private Emprestimo emprestimo(Usuario usuario) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(10L);
        emprestimo.setUsuario(usuario);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().minusDays(2));
        return emprestimo;
    }
}
