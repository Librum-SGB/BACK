package com.sgb.mylibrum;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.entities.enums.StatusExemplar;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import com.sgb.mylibrum.repositories.EditoraRepository;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.EstanteRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.ListaTarefaRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:api-fluxos;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.flyway.enabled=false"
        })
class ApiFluxosIntegrationTest {

    private static final String SENHA_ADMIN = "admin123";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FilialRepository filialRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ListaTarefaRepository listaTarefaRepository;
    @Autowired
    private EditoraRepository editoraRepository;
    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private EstanteRepository estanteRepository;
    @Autowired
    private ExemplarRepository exemplarRepository;
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @BeforeEach
    void limparDados() {
        emprestimoRepository.deleteAll();
        exemplarRepository.deleteAll();
        estanteRepository.deleteAll();
        materialRepository.deleteAll();
        listaTarefaRepository.deleteAll();
        usuarioRepository.deleteAll();
        editoraRepository.deleteAll();
        filialRepository.deleteAll();
    }

    @Test
    void fluxoDeLoginRetornaJwtQueAutorizaEndpointProtegido() throws Exception {
        Usuario admin = criarAdmin();

        HttpResult login = post("/auth/login",
                "{\"email\":\"" + admin.getEmail() + "\",\"senha\":\"" + SENHA_ADMIN + "\"}", null);

        assertThat(login.statusCode()).isEqualTo(200);
        JsonNode corpoLogin = jsonMapper.readTree(login.body());
        assertThat(corpoLogin.path("token").asText()).isNotBlank();
        assertThat(corpoLogin.path("type").asText()).isEqualTo("Bearer");
        assertThat(corpoLogin.path("funcao").asText()).isEqualTo("ADMIN");

        HttpResult emprestimos = get("/api/emprestimos", corpoLogin.path("token").asText());

        assertThat(emprestimos.statusCode()).isEqualTo(200);
        assertThat(jsonMapper.readTree(emprestimos.body()).isArray()).isTrue();
    }

    @Test
    void perfilAtualEChecklistUsamUsuarioAutenticado() throws Exception {
        Usuario admin = criarAdmin();
        String token = autenticar(admin.getEmail());

        HttpResult perfil = get("/api/usuarios/me", token);
        assertThat(perfil.statusCode()).isEqualTo(200);
        assertThat(jsonMapper.readTree(perfil.body()).path("id").asLong()).isEqualTo(admin.getId());

        HttpResult tarefa = post("/api/tarefas",
                """
                {
                  "descricao": "Conferir devoluções",
                  "prioridade": "MEDIA",
                  "status": "%s"
                }
                """.formatted(StatusTarefa.PENDENTE.name()), token);

        assertThat(tarefa.statusCode()).isEqualTo(201);
        JsonNode tarefaCriada = jsonMapper.readTree(tarefa.body());
        assertThat(tarefaCriada.path("usuarioId").asLong()).isEqualTo(admin.getId());
        assertThat(tarefaCriada.path("status").asText()).isEqualTo(StatusTarefa.PENDENTE.name());
    }

    @Test
    void fluxoDeEmprestimoEDevolucaoAtualizaEmprestimoEDisponibilidadeDoExemplar() throws Exception {
        DadosAcervo dados = criarDadosAcervo();
        String token = autenticar(dados.adminEmail());
        String corpoEmprestimo = """
                {
                  "usuarioId": %d,
                  "exemplarId": %d,
                  "usuarioResponsavelId": %d,
                  "dataDevolucaoPrevista": "%s"
                }
                """.formatted(dados.usuarioId(), dados.exemplarId(), dados.adminId(),
                LocalDate.now().plusDays(14));

        HttpResult criado = post("/api/emprestimos", corpoEmprestimo, token);

        assertThat(criado.statusCode()).isEqualTo(201);
        JsonNode emprestimoCriado = jsonMapper.readTree(criado.body());
        Long emprestimoId = emprestimoCriado.path("id").asLong();
        assertThat(emprestimoCriado.path("status").asText()).isEqualTo(StatusEmprestimo.ATIVO.name());
        assertThat(exemplarRepository.findById(dados.exemplarId()).orElseThrow().getStatus())
                .isEqualTo(StatusExemplar.EMPRESTADO);

        HttpResult devolvido = post("/api/emprestimos/" + emprestimoId + "/devolucao", "", token);

        assertThat(devolvido.statusCode()).isEqualTo(200);
        JsonNode emprestimoDevolvido = jsonMapper.readTree(devolvido.body());
        assertThat(emprestimoDevolvido.path("status").asText()).isEqualTo(StatusEmprestimo.DEVOLVIDO.name());
        assertThat(emprestimoDevolvido.path("dataDevolucaoEfetivada").isNull()).isFalse();
        Emprestimo persistido = emprestimoRepository.findById(emprestimoId).orElseThrow();
        assertThat(persistido.getStatus()).isEqualTo(StatusEmprestimo.DEVOLVIDO);
        assertThat(persistido.getDataDevolucaoEfetivada()).isNotNull();
        assertThat(exemplarRepository.findById(dados.exemplarId()).orElseThrow().getStatus())
                .isEqualTo(StatusExemplar.DISPONIVEL);
    }

    private Usuario criarAdmin() {
        Filial filial = criarFilial();
        Usuario admin = new Usuario();
        admin.setNome("Admin de integração");
        admin.setEmail("admin-" + UUID.randomUUID() + "@example.test");
        admin.setSenha(passwordEncoder.encode(SENHA_ADMIN));
        admin.setMatriculaFuncionario("M-" + UUID.randomUUID().toString().substring(0, 8));
        admin.setFuncao(FuncaoUsuario.ADMIN);
        admin.setFilial(filial);
        return usuarioRepository.saveAndFlush(admin);
    }

    private DadosAcervo criarDadosAcervo() {
        Usuario admin = criarAdmin();
        Usuario usuario = new Usuario();
        usuario.setNome("Leitor de integração");
        usuario.setEmail("leitor-" + UUID.randomUUID() + "@example.test");
        usuario.setSenha(passwordEncoder.encode("leitor123"));
        usuario.setFuncao(FuncaoUsuario.USUARIO);
        usuario.setFilial(admin.getFilial());
        usuario = usuarioRepository.saveAndFlush(usuario);

        Editora editora = new Editora();
        editora.setNome("Editora " + UUID.randomUUID());
        editora = editoraRepository.saveAndFlush(editora);

        Material material = new Material();
        material.setTitulo("Material de integração");
        material.setEditora(editora);
        material = materialRepository.saveAndFlush(material);

        Estante estante = new Estante();
        estante.setFilial(admin.getFilial());
        estante.setNome("Estante de integração");
        estante.setLocalizacao("Sala de testes");
        estante = estanteRepository.saveAndFlush(estante);

        Exemplar exemplar = new Exemplar();
        exemplar.setMaterial(material);
        exemplar.setFilial(admin.getFilial());
        exemplar.setEstante(estante);
        exemplar.setCodigoBarras("CB-" + UUID.randomUUID());
        exemplar.setStatus(StatusExemplar.DISPONIVEL);
        exemplar = exemplarRepository.saveAndFlush(exemplar);

        return new DadosAcervo(admin.getId(), admin.getEmail(), usuario.getId(), exemplar.getId());
    }

    private Filial criarFilial() {
        Filial filial = new Filial();
        filial.setNomeFantasia("Filial de integração");
        filial.setRazaoSocial("Filial de integração Ltda");
        filial.setCnpj(UUID.randomUUID().toString().replace("-", "").substring(0, 14));
        filial.setEndereco("Rua dos Testes, 1");
        filial.setBairro("Centro");
        filial.setCidade("São Paulo");
        filial.setEstado("SP");
        filial.setCep("01000000");
        filial.setAtivo(true);
        return filialRepository.saveAndFlush(filial);
    }

    private String autenticar(String email) throws Exception {
        HttpResult response = post("/auth/login",
                "{\"email\":\"" + email + "\",\"senha\":\"" + SENHA_ADMIN + "\"}", null);
        assertThat(response.statusCode()).isEqualTo(200);
        return jsonMapper.readTree(response.body()).path("token").asText();
    }

    private HttpResult get(String path, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(uri(path)).GET();
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
        return new HttpResult(response.statusCode(), response.body());
    }

    private HttpResult post(String path, String body, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        HttpResponse<String> response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
        return new HttpResult(response.statusCode(), response.body());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private record HttpResult(int statusCode, String body) {
    }

    private record DadosAcervo(Long adminId, String adminEmail, Long usuarioId, Long exemplarId) {
    }
}
