package com.sgb.mylibrum.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgb.mylibrum.entities.TokenRecuperacaoSenha;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.exceptions.TokenRecuperacaoInvalidoException;
import com.sgb.mylibrum.repositories.TokenRecuperacaoSenhaRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecuperacaoSenhaService {

    static final Duration VALIDADE_TOKEN = Duration.ofMinutes(30);
    static final Duration INTERVALO_MINIMO = Duration.ofMinutes(1);
    private static final String MENSAGEM_TOKEN_INVALIDO = "Token inválido ou expirado";

    private final SecureRandom secureRandom = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoSenhaRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String frontendUrl;

    public RecuperacaoSenhaService(UsuarioRepository usuarioRepository,
            TokenRecuperacaoSenhaRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Nunca revela se o e-mail existe: falhas e usuários inexistentes apenas são registrados em log.
     */
    @Transactional
    public void solicitar(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email.trim()).orElse(null);
        if (usuario == null || Boolean.FALSE.equals(usuario.getAtivo())) {
            return;
        }

        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC);
        if (tokenRepository.existsByUsuarioIdAndDataCriacaoAfter(usuario.getId(), agora.minus(INTERVALO_MINIMO))) {
            return;
        }
        tokenRepository.deleteByUsuarioIdAndUsadoEmIsNull(usuario.getId());

        String token = gerarToken();
        TokenRecuperacaoSenha entidade = new TokenRecuperacaoSenha();
        entidade.setUsuario(usuario);
        entidade.setTokenHash(hash(token));
        entidade.setExpiraEm(agora.plus(VALIDADE_TOKEN));
        tokenRepository.save(entidade);

        try {
            emailService.enviar(usuario.getFilial().getId(), usuario.getEmail(),
                    "Recuperação de senha - MyLibrum", montarMensagem(usuario, token));
        } catch (RuntimeException ex) {
            log.warn("Não foi possível enviar o e-mail de recuperação de senha do usuário {}: {}",
                    usuario.getId(), ex.getMessage());
            tokenRepository.delete(entidade);
        }
    }

    @Transactional
    public void redefinir(String token, String novaSenha) {
        TokenRecuperacaoSenha entidade = tokenRepository.findByTokenHash(hash(token.trim()))
                .orElseThrow(() -> new TokenRecuperacaoInvalidoException(MENSAGEM_TOKEN_INVALIDO));

        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC);
        if (entidade.getUsadoEm() != null || !entidade.getExpiraEm().isAfter(agora)) {
            throw new TokenRecuperacaoInvalidoException(MENSAGEM_TOKEN_INVALIDO);
        }

        Usuario usuario = entidade.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        entidade.setUsadoEm(agora);
        tokenRepository.save(entidade);
        tokenRepository.deleteByUsuarioIdAndUsadoEmIsNull(usuario.getId());
    }

    private String montarMensagem(Usuario usuario, String token) {
        String base = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
        return "Olá, " + usuario.getNome() + "!\n\n"
                + "Recebemos uma solicitação para redefinir a sua senha no MyLibrum.\n"
                + "Acesse o link abaixo para criar uma nova senha (válido por "
                + VALIDADE_TOKEN.toMinutes() + " minutos):\n\n"
                + base + "/redefinir-senha?token=" + token + "\n\n"
                + "Se você não fez essa solicitação, ignore este e-mail.";
    }

    private String gerarToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponível", ex);
        }
    }
}
