package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.mapper.UsuarioMapper;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.HistoricoMultaRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;
    @Mock
    private EmprestimoRepository emprestimoRepository;
    @Mock
    private HistoricoMultaRepository historicoMultaRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(
                repository, passwordEncoder, emprestimoRepository, historicoMultaRepository, new UsuarioMapper());
    }

    @Test
    void deveRejeitarEmailDuplicadoIgnorandoMaiusculasEMinusculas() {
        when(repository.existsByEmailIgnoreCase("existente@exemplo.com")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.create(usuarioDto(
                " Existente@Exemplo.com ", null)));
    }

    @Test
    void deveRejeitarMatriculaDuplicadaIgnorandoMaiusculasEMinusculas() {
        when(repository.existsByEmailIgnoreCase("novo@exemplo.com")).thenReturn(false);
        when(repository.existsByMatriculaFuncionarioIgnoreCase("MAT123")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.create(usuarioDto(
                "novo@exemplo.com", "mat123")));
    }

    @Test
    void deveRejeitarExclusaoComEmprestimosAtivosOuAtrasados() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(emprestimoRepository.existsByUsuarioIdAndStatusIn(
                1L, List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO)))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.delete(1L));
    }

    @Test
    void deveRejeitarExclusaoDeUsuarioAdmin() {
        Usuario admin = usuario();
        admin.setFuncao(FuncaoUsuario.ADMIN);
        when(repository.findById(1L)).thenReturn(Optional.of(admin));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.delete(1L));

        assertEquals("Não é possível excluir um usuário com função Admin", exception.getMessage());
        verify(emprestimoRepository, never()).existsByUsuarioIdAndStatusIn(any(), any());
        verify(repository, never()).delete(any(Usuario.class));
    }

    @Test
    void deveMarcarUsuarioComoExcluidoSemApagaLoFisicamente() {
        Usuario usuario = usuario();
        usuario.setFuncao(FuncaoUsuario.USUARIO);
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(emprestimoRepository.existsByUsuarioIdAndStatusIn(
                1L, List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO)))
                .thenReturn(false);

        service.delete(1L);

        assertEquals(true, usuario.getExcluido());
        verify(repository).save(usuario);
        verify(repository, never()).delete(any(Usuario.class));
    }

    @Test
    void deveAtualizarBloqueioQuandoNaoHaMultasPendentes() {
        Usuario usuario = usuario();
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var atualizado = service.atualizarBloqueio(1L, true);

        assertEquals(true, atualizado.getEstaBloqueado());
        verify(repository).save(usuario);
    }

    @Test
    void naoDeveDesbloquearUsuarioComMultaPendente() {
        Usuario usuario = usuario();
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(historicoMultaRepository.existsByUsuarioIdAndStatus(1L, StatusMulta.PENDENTE))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.atualizarBloqueio(1L, false));
    }

    @Test
    void deveBuscarUsuariosPorNomeEMatricula() {
        var pageable = PageRequest.of(0, 20);
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty(pageable));

        assertEquals(0, service.findAll(" ana ", "mat123", null, pageable).getTotalElements());
        verify(repository).findAll(any(Specification.class), any(Pageable.class));
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário teste");
        usuario.setEmail("teste@exemplo.com");
        usuario.setEstaBloqueado(false);
        return usuario;
    }

    private UsuarioRequestDTO usuarioDto(String email, String matricula) {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNome("Usuário de teste");
        dto.setEmail(email);
        dto.setSenha("senha");
        dto.setFilialId(1L);
        dto.setMatriculaFuncionario(matricula);
        dto.setFuncao(FuncaoUsuario.USUARIO);
        return dto;
    }
}
