package com.sgb.mylibrum.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutorizacaoService {

    private static final String PREFIXO_ROLE = "ROLE_";

    private final UsuarioRepository usuarioRepository;

    public FuncaoUsuario funcaoLogada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("Acesso negado");
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role != null && role.startsWith(PREFIXO_ROLE)) {
                try {
                    return FuncaoUsuario.valueOf(role.substring(PREFIXO_ROLE.length()));
                } catch (IllegalArgumentException ignored) {
                    // autoridade desconhecida
                }
            }
        }
        throw new AccessDeniedException("Acesso negado");
    }

    public Long idLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Acesso negado");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Acesso negado"))
                .getId();
    }

    /** USUARIO só acessa dados próprios; as demais funções passam (a rota já restringe a equipe). */
    public void exigirProprioOuEquipe(Long usuarioId) {
        if (funcaoLogada() == FuncaoUsuario.USUARIO && !idLogado().equals(usuarioId)) {
            throw new AccessDeniedException("Acesso negado");
        }
    }

    public void exigirProprio(Long usuarioId) {
        if (usuarioId == null || !idLogado().equals(usuarioId)) {
            throw new AccessDeniedException("Acesso negado");
        }
    }

    /** Define se a função logada pode atribuir a função informada a uma conta. */
    public void exigirPodeAtribuirFuncao(FuncaoUsuario funcaoAlvo) {
        FuncaoUsuario logada = funcaoLogada();
        if (logada == FuncaoUsuario.ADMIN) {
            return;
        }
        if (logada == FuncaoUsuario.BIBLIOTECARIA && ehAssistenteOuUsuario(funcaoAlvo)) {
            return;
        }
        throw new AccessDeniedException("Acesso negado");
    }

    /**
     * Admin gerencia qualquer conta. Bibliotecária gerencia assistentes e usuários gerais
     * (e, quando permitido, o próprio registro).
     */
    public void exigirPodeGerenciarConta(Long alvoId, FuncaoUsuario funcaoAlvo, boolean permitirProprio) {
        FuncaoUsuario logada = funcaoLogada();
        if (logada == FuncaoUsuario.ADMIN) {
            return;
        }
        if (logada == FuncaoUsuario.BIBLIOTECARIA) {
            if (ehAssistenteOuUsuario(funcaoAlvo) || (permitirProprio && idLogado().equals(alvoId))) {
                return;
            }
        }
        throw new AccessDeniedException("Acesso negado");
    }

    private boolean ehAssistenteOuUsuario(FuncaoUsuario funcao) {
        return funcao == null || funcao == FuncaoUsuario.ASSISTENTE || funcao == FuncaoUsuario.USUARIO;
    }
}
