package com.sgb.mylibrum.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockFilterChain;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        MDC.clear();
        filter = new JwtAuthenticationFilter(jwtUtil);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    void deveCriarAutenticacaoComPerfilDoTokenValido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        when(jwtUtil.validateToken("token-valido")).thenReturn(true);
        when(jwtUtil.getUsernameFromToken("token-valido")).thenReturn("leitor@exemplo.com");
        when(jwtUtil.getFuncaoFromToken("token-valido")).thenReturn("USUARIO");

        filter.doFilter(request, response, chain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals("leitor@exemplo.com", authentication.getPrincipal());
        assertEquals("ROLE_USUARIO", authentication.getAuthorities().iterator().next().getAuthority());
        assertEquals("USUARIO", MDC.get("perfil"));
        verify(jwtUtil).validateToken("token-valido");
    }

    @Test
    void tokenInvalidoNaoAutenticaRequisicao() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer expirado");
        when(jwtUtil.validateToken("expirado")).thenReturn(false);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).validateToken("expirado");
    }

    @Test
    void requisicaoSemBearerSegueSemValidarToken() throws Exception {
        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenSemFuncaoCriaAutenticacaoSemAutoridades() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer sem-funcao");
        when(jwtUtil.validateToken("sem-funcao")).thenReturn(true);
        when(jwtUtil.getUsernameFromToken("sem-funcao")).thenReturn("usuario");
        when(jwtUtil.getFuncaoFromToken("sem-funcao")).thenReturn(null);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertEquals(0, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
        assertEquals("-", MDC.get("perfil"));
    }
}
