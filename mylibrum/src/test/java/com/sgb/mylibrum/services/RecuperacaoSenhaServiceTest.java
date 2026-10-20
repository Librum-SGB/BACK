package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.TokenRecuperacaoSenha;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.exceptions.TokenRecuperacaoInvalidoException;
import com.sgb.mylibrum.repositories.TokenRecuperacaoSenhaRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class RecuperacaoSenhaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TokenRecuperacaoSenhaRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    private RecuperacaoSenhaService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new RecuperacaoSenhaService(usuarioRepository, tokenRepository, passwordEncoder, emailService,
                "http://front.test/");
        Filial filial = new Filial();
        filial.setId(4L);
        usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNome("Maria");
        usuario.setEmail("maria@exemplo.com");
        usuario.setFilial(filial);
    }

    @Test
    void solicitarGeraTokenArmazenaApenasHashEEnviaLink() {
        when(usuarioRepository.findByEmail("maria@exemplo.com")).thenReturn(Optional.of(usuario));
        when(tokenRepository.existsByUsuarioIdAndDataCriacaoAfter(eq(7L), any())).thenReturn(false);

        service.solicitar(" maria@exemplo.com ");

        ArgumentCaptor<TokenRecuperacaoSenha> salvo = ArgumentCaptor.forClass(TokenRecuperacaoSenha.class);
        verify(tokenRepository).deleteByUsuarioIdAndUsadoEmIsNull(7L);
        verify(tokenRepository).save(salvo.capture());
        ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
        verify(emailService).enviar(eq(4L), eq("maria@exemplo.com"), anyString(), texto.capture());

        String mensagem = texto.getValue();
        String marcador = "http://front.test/redefinir-senha?token=";
        assertTrue(mensagem.contains(marcador));
        String token = mensagem.substring(mensagem.indexOf(marcador) + marcador.length()).lines().findFirst().get();
        assertEquals(RecuperacaoSenhaService.hash(token), salvo.getValue().getTokenHash());
        assertTrue(salvo.getValue().getExpiraEm().isAfter(OffsetDateTime.now(ZoneOffset.UTC)));
    }

    @Test
    void solicitarIgnoraEmailInexistenteSemEnviarNada() {
        when(usuarioRepository.findByEmail("x@exemplo.com")).thenReturn(Optional.empty());

        service.solicitar("x@exemplo.com");

        verify(emailService, never()).enviar(anyLong(), anyString(), anyString(), anyString());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void solicitarRespeitaIntervaloMinimoEntreSolicitacoes() {
        when(usuarioRepository.findByEmail("maria@exemplo.com")).thenReturn(Optional.of(usuario));
        when(tokenRepository.existsByUsuarioIdAndDataCriacaoAfter(eq(7L), any())).thenReturn(true);

        service.solicitar("maria@exemplo.com");

        verify(tokenRepository, never()).save(any());
        verify(emailService, never()).enviar(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void solicitarDescartaTokenQuandoEnvioFalha() {
        when(usuarioRepository.findByEmail("maria@exemplo.com")).thenReturn(Optional.of(usuario));
        when(tokenRepository.existsByUsuarioIdAndDataCriacaoAfter(eq(7L), any())).thenReturn(false);
        doThrow(new IllegalStateException("SMTP não configurado"))
                .when(emailService).enviar(anyLong(), anyString(), anyString(), anyString());

        service.solicitar("maria@exemplo.com");

        verify(tokenRepository).delete(any(TokenRecuperacaoSenha.class));
    }

    @Test
    void redefinirAtualizaSenhaEMarcaTokenComoUsado() {
        TokenRecuperacaoSenha token = tokenValido();
        when(tokenRepository.findByTokenHash(RecuperacaoSenhaService.hash("abc"))).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("novaSenha1")).thenReturn("hash-bcrypt");

        service.redefinir("abc", "novaSenha1");

        assertEquals("hash-bcrypt", usuario.getSenha());
        assertNotNull(token.getUsadoEm());
        verify(usuarioRepository).save(usuario);
        verify(tokenRepository).deleteByUsuarioIdAndUsadoEmIsNull(7L);
    }

    @Test
    void redefinirRejeitaTokenExpirado() {
        TokenRecuperacaoSenha token = tokenValido();
        token.setExpiraEm(OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(1));
        when(tokenRepository.findByTokenHash(RecuperacaoSenhaService.hash("abc"))).thenReturn(Optional.of(token));

        assertThrows(TokenRecuperacaoInvalidoException.class, () -> service.redefinir("abc", "novaSenha1"));
        assertNull(usuario.getSenha());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void redefinirRejeitaTokenJaUtilizado() {
        TokenRecuperacaoSenha token = tokenValido();
        token.setUsadoEm(OffsetDateTime.now(ZoneOffset.UTC));
        when(tokenRepository.findByTokenHash(RecuperacaoSenhaService.hash("abc"))).thenReturn(Optional.of(token));

        assertThrows(TokenRecuperacaoInvalidoException.class, () -> service.redefinir("abc", "novaSenha1"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void redefinirRejeitaTokenDesconhecido() {
        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThrows(TokenRecuperacaoInvalidoException.class, () -> service.redefinir("zzz", "novaSenha1"));
    }

    private TokenRecuperacaoSenha tokenValido() {
        TokenRecuperacaoSenha token = new TokenRecuperacaoSenha();
        token.setUsuario(usuario);
        token.setTokenHash(RecuperacaoSenhaService.hash("abc"));
        token.setExpiraEm(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(10));
        return token;
    }
}
