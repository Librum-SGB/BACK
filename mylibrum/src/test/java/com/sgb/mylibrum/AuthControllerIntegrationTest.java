package com.sgb.mylibrum;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        filialRepository.deleteAll();

        Filial filial = new Filial();
        filial.setNomeFantasia("Filial Teste");
        filial.setRazaoSocial("Razao Social Teste");
        filial.setCnpj("12345678000199");
        filial.setEndereco("Rua A");
        filial.setBairro("Centro");
        filial.setCidade("São Paulo");
        filial.setEstado("SP");
        filial.setCep("01000000");
        filial.setAtivo(true);
        filial = filialRepository.save(filial);

        Usuario admin = new Usuario();
        admin.setNome("Admin Teste");
        admin.setEmail("admin@gmail.com");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setMatriculaFuncionario("M-001");
        admin.setFuncao(FuncaoUsuario.ADMIN);
        admin.setFilial(filial);
        usuarioRepository.save(admin);

        Usuario usuario = new Usuario();
        usuario.setNome("Usuário Teste");
        usuario.setCpf("12345678901");
        usuario.setEmail("usuario@gmail.com");
        usuario.setSenha(passwordEncoder.encode("user123"));
        usuario.setTelefone("11987654321");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setFuncao(FuncaoUsuario.USUARIO);
        usuario.setFilial(filial);
        usuarioRepository.save(usuario);
    }

    @Test
    void shouldLoginAndReturnJwtToken() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\"email\":\"admin@gmail.com\",\"senha\":\"admin123\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("token");
        assertThat(response.body()).contains("Bearer");
        assertThat(response.body()).contains("\"funcao\":\"ADMIN\"");
    }

    @Test
    void shouldLoginUsuarioAndReturnJwtToken() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\"email\":\"usuario@gmail.com\",\"senha\":\"user123\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("token");
        assertThat(response.body()).contains("Bearer");
    }
}
