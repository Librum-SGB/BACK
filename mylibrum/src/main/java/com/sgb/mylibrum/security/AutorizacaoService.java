package com.sgb.mylibrum.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutorizacaoService {

    private static final String PREFIXO_ROLE = "ROLE_";

    private final UsuarioRepository usuarioRepository;

    public FuncaoUsuario funcaoLogada() {
        log.debug("Consultando funcao do usuario autenticado");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("Acesso negado: autenticacao ausente ao consultar funcao");
            throw new AccessDeniedException("Acesso negado");
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role != null && role.startsWith(PREFIXO_ROLE)) {
                try {
                    return FuncaoUsuario.valueOf(role.substring(PREFIXO_ROLE.length()));
                } catch (IllegalArgumentException ignored) {
                    log.error("Falha ao interpretar funcao da autoridade autenticada", ignored);
                    // autoridade desconhecida
                }
            }
        }
        log.warn("Acesso negado: nenhuma funcao autorizada na autenticacao");
        throw new AccessDeniedException("Acesso negado");
    }

    public Long idLogado() {
        log.debug("Consultando id do usuario autenticado");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            log.warn("Acesso negado: autenticacao ou identificador ausente");
            throw new AccessDeniedException("Acesso negado");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> {
                    log.warn("Acesso negado: usuario autenticado nao localizado");
                    return new AccessDeniedException("Acesso negado");
                })
                .getId();
    }

    public Long filialLogadaId() {
        log.debug("Consultando filial do usuario autenticado");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            log.warn("Acesso negado: autenticacao ou identificador ausente");
            throw new AccessDeniedException("Acesso negado");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> {
                    log.warn("Acesso negado: usuario autenticado nao localizado");
                    return new AccessDeniedException("Acesso negado");
                })
                .getFilial().getId();
    }

    /** USUARIO só acessa dados próprios; as demais funções passam (a rota já restringe a equipe). */
    public void exigirProprioOuEquipe(Long usuarioId) {
        if (funcaoLogada() == FuncaoUsuario.USUARIO && !idLogado().equals(usuarioId)) {
            log.warn("Acesso negado a dados do usuarioId={}", usuarioId);
            throw new AccessDeniedException("Acesso negado");
        }
    }

    public void exigirProprio(Long usuarioId) {
        if (usuarioId == null || !idLogado().equals(usuarioId)) {
            log.warn("Acesso proprio negado para usuarioId={}", usuarioId);
            throw new AccessDeniedException("Acesso negado");
        }
    }

    /** Define se a função logada pode atribuir a função informada a uma conta. */
    public void exigirPodeAtribuirFuncao(FuncaoUsuario funcaoAlvo) {
        log.debug("Validando atribuicao de funcao: funcaoAlvo={}", funcaoAlvo);
        FuncaoUsuario logada = funcaoLogada();
        if (logada == FuncaoUsuario.ADMIN) {
            return;
        }
        if (logada == FuncaoUsuario.BIBLIOTECARIA && ehAssistenteOuUsuario(funcaoAlvo)) {
            return;
        }
        log.warn("Atribuicao de funcao negada: funcaoSolicitada={}, funcaoLogada={}", funcaoAlvo, logada);
        throw new AccessDeniedException("Acesso negado");
    }

    /**
     * Admin gerencia qualquer conta. Bibliotecária gerencia assistentes e usuários gerais
     * (e, quando permitido, o próprio registro).
     */
    public void exigirPodeGerenciarConta(Long alvoId, FuncaoUsuario funcaoAlvo, boolean permitirProprio) {
        log.debug("Validando gerenciamento de conta: alvoId={}, funcaoAlvo={}", alvoId, funcaoAlvo);
        FuncaoUsuario logada = funcaoLogada();
        if (logada == FuncaoUsuario.ADMIN) {
            return;
        }
        if (logada == FuncaoUsuario.BIBLIOTECARIA) {
            if (ehAssistenteOuUsuario(funcaoAlvo) || (permitirProprio && idLogado().equals(alvoId))) {
                return;
            }
        }
        log.warn("Gerenciamento de conta negado: alvoId={}, funcaoAlvo={}, funcaoLogada={}", alvoId, funcaoAlvo, logada);
        throw new AccessDeniedException("Acesso negado");
    }

    private boolean ehAssistenteOuUsuario(FuncaoUsuario funcao) {
        return funcao == null || funcao == FuncaoUsuario.ASSISTENTE || funcao == FuncaoUsuario.USUARIO;
    }
}
