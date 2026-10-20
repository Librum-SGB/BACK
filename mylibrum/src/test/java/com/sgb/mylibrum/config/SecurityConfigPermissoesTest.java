package com.sgb.mylibrum.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.security.JwtAuthenticationFilter;
import com.sgb.mylibrum.security.JwtUtil;

import jakarta.servlet.Filter;

class SecurityConfigPermissoesTest {

    private static final String SECRET = "bXlsaWJydW0tc2VjcmV0LWtleS1mb3ItZGV2LW9ubHktMzItYnl0ZXM=";

    private AnnotationConfigWebApplicationContext context;
    private MockMvc mockMvc;
    private JwtUtil jwtUtil;

    @RestController
    static class ProbeController {
        @RequestMapping({ "/api/**", "/outra" })
        String qualquer() {
            return "ok";
        }
    }

    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import({ SecurityConfig.class, ProbeController.class })
    static class TestConfig {
        @Bean
        JwtUtil jwtUtil() {
            return new JwtUtil(SECRET, 3_600_000L);
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
            return new JwtAuthenticationFilter(jwtUtil);
        }
    }

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(TestConfig.class);
        context.refresh();
        jwtUtil = context.getBean(JwtUtil.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
                .build();
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    private int status(FuncaoUsuario funcao, HttpMethod metodo, String path) throws Exception {
        var builder = request(metodo, path);
        if (funcao != null) {
            builder.header("Authorization", "Bearer " + jwtUtil.generateToken("u@x.com", funcao));
        }
        return mockMvc.perform(builder).andReturn().getResponse().getStatus();
    }

    private void esperar(HttpMethod metodo, String path, boolean admin, boolean biblio, boolean assist, boolean usuario)
            throws Exception {
        verificar(FuncaoUsuario.ADMIN, metodo, path, admin);
        verificar(FuncaoUsuario.BIBLIOTECARIA, metodo, path, biblio);
        verificar(FuncaoUsuario.ASSISTENTE, metodo, path, assist);
        verificar(FuncaoUsuario.USUARIO, metodo, path, usuario);
    }

    private void verificar(FuncaoUsuario funcao, HttpMethod metodo, String path, boolean permitido) throws Exception {
        int esperado = permitido ? 200 : 403;
        assertEquals(esperado, status(funcao, metodo, path), funcao + " " + metodo + " " + path);
    }

    @Test
    void semTokenRetorna401() throws Exception {
        assertEquals(401, status(null, HttpMethod.GET, "/api/materiais"));
    }

    @Test
    void acervoLeituraParaTodosEscritaParaEquipeExclusaoSoAdminEBibliotecaria() throws Exception {
        for (String base : new String[] { "/api/materiais", "/api/exemplares", "/api/autores", "/api/editoras", "/api/generos" }) {
            esperar(HttpMethod.GET, base, true, true, true, true);
            esperar(HttpMethod.GET, base + "/1", true, true, true, true);
            esperar(HttpMethod.POST, base, true, true, true, false);
            esperar(HttpMethod.PUT, base + "/1", true, true, true, false);
            esperar(HttpMethod.DELETE, base + "/1", true, true, false, false);
        }
        esperar(HttpMethod.PUT, "/api/generos/desativar/1", true, true, true, false);
    }

    @Test
    void estantesSomenteEquipe() throws Exception {
        esperar(HttpMethod.GET, "/api/estantes", true, true, true, false);
        esperar(HttpMethod.POST, "/api/estantes", true, true, true, false);
        esperar(HttpMethod.DELETE, "/api/estantes/1", true, true, false, false);
    }

    @Test
    void emprestimos() throws Exception {
        esperar(HttpMethod.GET, "/api/emprestimos", true, true, true, false);
        esperar(HttpMethod.GET, "/api/emprestimos/1", true, true, true, true);
        esperar(HttpMethod.GET, "/api/emprestimos/usuarios/1", true, true, true, true);
        esperar(HttpMethod.POST, "/api/emprestimos", true, true, true, false);
        esperar(HttpMethod.POST, "/api/emprestimos/1/devolucao", true, true, true, false);
        esperar(HttpMethod.POST, "/api/emprestimos/1/renovacao", true, true, true, false);
        esperar(HttpMethod.PUT, "/api/emprestimos/1", true, true, false, false);
        esperar(HttpMethod.DELETE, "/api/emprestimos/1", true, true, false, false);
    }

    @Test
    void multas() throws Exception {
        esperar(HttpMethod.GET, "/api/historico-multas", true, true, true, false);
        esperar(HttpMethod.GET, "/api/historico-multas/1", true, true, true, true);
        esperar(HttpMethod.GET, "/api/historico-multas/usuarios/1", true, true, true, true);
        esperar(HttpMethod.POST, "/api/historico-multas", true, true, false, false);
        esperar(HttpMethod.PATCH, "/api/historico-multas/1/pagamento", true, true, true, false);
        esperar(HttpMethod.PUT, "/api/historico-multas/1", true, true, false, false);
        esperar(HttpMethod.DELETE, "/api/historico-multas/1", true, true, false, false);
    }

    @Test
    void usuarios() throws Exception {
        esperar(HttpMethod.GET, "/api/usuarios", true, true, true, false);
        esperar(HttpMethod.GET, "/api/usuarios/1", true, true, true, true);
        esperar(HttpMethod.POST, "/api/usuarios", true, true, false, false);
        esperar(HttpMethod.PUT, "/api/usuarios/1", true, true, false, true);
        esperar(HttpMethod.PATCH, "/api/usuarios/1/bloqueio", true, true, false, false);
        esperar(HttpMethod.DELETE, "/api/usuarios/1", true, true, false, false);
    }

    @Test
    void configuracoesSomenteAdmin() throws Exception {
        esperar(HttpMethod.GET, "/api/configuracoes", true, false, false, false);
        esperar(HttpMethod.PUT, "/api/configuracoes/1", true, false, false, false);
    }

    @Test
    void filiaisLeituraEquipeEscritaAdmin() throws Exception {
        esperar(HttpMethod.GET, "/api/filiais", true, true, true, false);
        esperar(HttpMethod.POST, "/api/filiais", true, false, false, false);
        esperar(HttpMethod.DELETE, "/api/filiais/1", true, false, false, false);
    }

    @Test
    void tarefasParaTodosAutenticados() throws Exception {
        esperar(HttpMethod.GET, "/api/tarefas", true, true, true, true);
        esperar(HttpMethod.GET, "/api/tarefas/gestores/1", true, true, true, true);
        esperar(HttpMethod.POST, "/api/tarefas", true, true, true, true);
        esperar(HttpMethod.PUT, "/api/tarefas/1", true, true, true, true);
        esperar(HttpMethod.DELETE, "/api/tarefas/1", true, true, true, true);
    }

    @Test
    void rotaDesconhecidaNegada() throws Exception {
        esperar(HttpMethod.GET, "/outra", false, false, false, false);
    }
}
