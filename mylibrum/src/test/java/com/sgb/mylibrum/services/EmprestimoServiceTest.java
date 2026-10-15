package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.dtos.mapper.EmprestimoMapper;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.entities.enums.StatusExemplar;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository repository;
    @Mock
    private ExemplarRepository exemplarRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ConfiguracaoRepository configuracaoRepository;

    private EmprestimoService service;

    @BeforeEach
    void setUp() {
        service = new EmprestimoService(
                repository, exemplarRepository, usuarioRepository, configuracaoRepository, new EmprestimoMapper());
    }

    @Test
    void deveCriarEmprestimoEMarcarExemplarComoEmprestado() {
        Usuario usuario = usuario(1L, false);
        Usuario responsavel = usuario(2L, false);
        Exemplar exemplar = exemplar(StatusExemplar.DISPONIVEL);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsavel));
        when(exemplarRepository.findByIdForUpdate(3L)).thenReturn(Optional.of(exemplar));
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmprestimoRequestDTO dto = new EmprestimoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setUsuarioResponsavelId(2L);
        dto.setExemplarId(3L);
        dto.setDataDevolucaoPrevista(LocalDate.now().plusDays(14));

        var result = service.create(dto);

        assertEquals(StatusEmprestimo.ATIVO, result.getStatus());
        assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());
        verify(exemplarRepository).save(exemplar);
    }

    @Test
    void deveBuscarTodosEmprestimosDoUsuario() {
        when(repository.findAll(any(Specification.class))).thenReturn(List.of());

        var resultado = service.findByUsuarioId(1L, null);

        assertEquals(List.of(), resultado);
        verify(repository).findAll(any(Specification.class));
    }

    @Test
    void naoDeveCriarEmprestimoParaUsuarioBloqueado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        EmprestimoRequestDTO dto = new EmprestimoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setExemplarId(3L);
        dto.setUsuarioResponsavelId(2L);

        assertThrows(IllegalStateException.class, () -> service.create(dto));
    }

    @Test
    void naoDeveCriarEmprestimoComExemplarIndisponivel() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, false)));
        when(exemplarRepository.findByIdForUpdate(3L))
                .thenReturn(Optional.of(exemplar(StatusExemplar.EMPRESTADO)));
        EmprestimoRequestDTO dto = new EmprestimoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setExemplarId(3L);
        dto.setUsuarioResponsavelId(2L);

        assertThrows(IllegalStateException.class, () -> service.create(dto));
    }

    @Test
    void deveRegistrarDevolucaoEDeixarExemplarDisponivel() {
        Exemplar exemplar = exemplar(StatusExemplar.EMPRESTADO);
        Emprestimo emprestimo = emprestimo(exemplar);
        when(repository.findByIdForUpdate(10L)).thenReturn(Optional.of(emprestimo));
        when(exemplarRepository.findByIdForUpdate(any())).thenReturn(Optional.of(exemplar));
        when(repository.save(emprestimo)).thenReturn(emprestimo);

        var result = service.devolver(10L);

        assertEquals(StatusEmprestimo.DEVOLVIDO, result.getStatus());
        assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
        verify(exemplarRepository).save(exemplar);
    }

    @Test
    void deveRenovarDentroDoLimiteConfigurado() {
        Usuario usuario = usuario(1L, false);
        Filial filial = new Filial();
        filial.setId(5L);
        usuario.setFilial(filial);
        Emprestimo emprestimo = emprestimo(exemplar(StatusExemplar.EMPRESTADO));
        emprestimo.setUsuario(usuario);
        emprestimo.setDataSaida(OffsetDateTime.now());
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(14));
        emprestimo.setRenovacoesContagem(0);
        Configuracao configuracao = new Configuracao();
        configuracao.setValor("2");

        when(repository.findByIdForUpdate(10L)).thenReturn(Optional.of(emprestimo));
        when(configuracaoRepository.findByFilialIdAndChave(5L, "emprestimo.renovacoes.max"))
                .thenReturn(Optional.of(configuracao));
        when(repository.save(emprestimo)).thenReturn(emprestimo);

        var result = service.renovar(10L);

        assertEquals(1, result.getRenovacoesContagem());
        assertEquals(LocalDate.now().plusDays(28), result.getDataDevolucaoPrevista());
    }

    @Test
    void naoDeveRenovarAoAtingirLimiteConfigurado() {
        Usuario usuario = usuario(1L, false);
        Filial filial = new Filial();
        filial.setId(5L);
        usuario.setFilial(filial);
        Emprestimo emprestimo = emprestimo(exemplar(StatusExemplar.EMPRESTADO));
        emprestimo.setUsuario(usuario);
        emprestimo.setDataSaida(OffsetDateTime.now().minusDays(14));
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(14));
        emprestimo.setRenovacoesContagem(1);
        Configuracao configuracao = new Configuracao();
        configuracao.setValor("1");
        when(repository.findByIdForUpdate(10L)).thenReturn(Optional.of(emprestimo));
        when(configuracaoRepository.findByFilialIdAndChave(5L, "emprestimo.renovacoes.max"))
                .thenReturn(Optional.of(configuracao));

        assertThrows(IllegalStateException.class, () -> service.renovar(10L));
    }

    @Test
    void deveMarcarEmprestimosVencidosComoAtrasados() {
        Emprestimo emprestimo = emprestimo(exemplar(StatusExemplar.EMPRESTADO));
        when(repository.findByStatusInAndDataDevolucaoPrevistaBefore(
                org.mockito.ArgumentMatchers.eq(List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO)),
                any(LocalDate.class)))
                .thenReturn(List.of(emprestimo));

        service.marcarEmprestimosAtrasados();

        assertEquals(StatusEmprestimo.ATRASADO, emprestimo.getStatus());
        verify(repository).saveAll(List.of(emprestimo));
    }

    private Usuario usuario(Long id, boolean bloqueado) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEstaBloqueado(bloqueado);
        return usuario;
    }

    private Exemplar exemplar(StatusExemplar status) {
        Exemplar exemplar = new Exemplar();
        exemplar.setId(3L);
        exemplar.setStatus(status);
        return exemplar;
    }

    private Emprestimo emprestimo(Exemplar exemplar) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(10L);
        emprestimo.setExemplar(exemplar);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(7));
        return emprestimo;
    }
}
