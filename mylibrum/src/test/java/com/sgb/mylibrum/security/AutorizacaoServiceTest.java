package com.sgb.mylibrum.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.repositories.UsuarioRepository;

class AutorizacaoServiceTest {

    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final AutorizacaoService service = new AutorizacaoService(repository);

    @AfterEach
    void limpar() {
        SecurityContextHolder.clearContext();
    }

    private void logarComo(FuncaoUsuario funcao, long id) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "u" + id + "@x.com", null, List.of(new SimpleGrantedAuthority("ROLE_" + funcao))));
        Usuario usuario = new Usuario();
        usuario.setId(id);
        lenient().when(repository.findByEmail("u" + id + "@x.com")).thenReturn(Optional.of(usuario));
    }

    @Test
    void usuarioGeralSoAcessaDadosProprios() {
        logarComo(FuncaoUsuario.USUARIO, 5L);
        assertDoesNotThrow(() -> service.exigirProprioOuEquipe(5L));
        assertThrows(AccessDeniedException.class, () -> service.exigirProprioOuEquipe(6L));
    }

    @Test
    void equipeAcessaDadosDeQualquerUsuario() {
        logarComo(FuncaoUsuario.ASSISTENTE, 2L);
        assertDoesNotThrow(() -> service.exigirProprioOuEquipe(6L));
    }

    @Test
    void adminGerenciaQualquerConta() {
        logarComo(FuncaoUsuario.ADMIN, 1L);
        assertDoesNotThrow(() -> service.exigirPodeGerenciarConta(9L, FuncaoUsuario.BIBLIOTECARIA, false));
        assertDoesNotThrow(() -> service.exigirPodeAtribuirFuncao(FuncaoUsuario.ADMIN));
    }

    @Test
    void bibliotecariaGerenciaApenasAssistentesUsuariosEOProprioRegistro() {
        logarComo(FuncaoUsuario.BIBLIOTECARIA, 3L);
        assertDoesNotThrow(() -> service.exigirPodeGerenciarConta(10L, FuncaoUsuario.ASSISTENTE, false));
        assertDoesNotThrow(() -> service.exigirPodeGerenciarConta(11L, FuncaoUsuario.USUARIO, false));
        assertThrows(AccessDeniedException.class,
                () -> service.exigirPodeGerenciarConta(12L, FuncaoUsuario.BIBLIOTECARIA, true));
        assertThrows(AccessDeniedException.class,
                () -> service.exigirPodeGerenciarConta(1L, FuncaoUsuario.ADMIN, true));
        assertDoesNotThrow(() -> service.exigirPodeGerenciarConta(3L, FuncaoUsuario.BIBLIOTECARIA, true));
        assertThrows(AccessDeniedException.class,
                () -> service.exigirPodeGerenciarConta(3L, FuncaoUsuario.BIBLIOTECARIA, false));
    }

    @Test
    void bibliotecariaNaoAtribuiFuncoesElevadas() {
        logarComo(FuncaoUsuario.BIBLIOTECARIA, 3L);
        assertDoesNotThrow(() -> service.exigirPodeAtribuirFuncao(FuncaoUsuario.ASSISTENTE));
        assertThrows(AccessDeniedException.class, () -> service.exigirPodeAtribuirFuncao(FuncaoUsuario.ADMIN));
        assertThrows(AccessDeniedException.class, () -> service.exigirPodeAtribuirFuncao(FuncaoUsuario.BIBLIOTECARIA));
    }

    @Test
    void exigirProprioComparaComUsuarioLogado() {
        logarComo(FuncaoUsuario.BIBLIOTECARIA, 3L);
        assertEquals(3L, service.idLogado());
        assertDoesNotThrow(() -> service.exigirProprio(3L));
        assertThrows(AccessDeniedException.class, () -> service.exigirProprio(4L));
        assertThrows(AccessDeniedException.class, () -> service.exigirProprio(null));
    }
}
