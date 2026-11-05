package com.sgb.mylibrum.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import com.sgb.mylibrum.dtos.request.AutorRequestDTO;
import com.sgb.mylibrum.dtos.request.CadastroIsbnRequestDTO;
import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.request.HistoricoMultaRequestDTO;
import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.AutorResponseDTO;
import com.sgb.mylibrum.dtos.response.CadastroIsbnResponseDTO;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.dtos.response.ConsultaIsbnResponseDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
import com.sgb.mylibrum.dtos.response.HistoricoMultaResponseDTO;
import com.sgb.mylibrum.dtos.response.ListaTarefaResponseDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import com.sgb.mylibrum.exceptions.GlobalExceptionHandler;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import com.sgb.mylibrum.security.AutorizacaoService;
import com.sgb.mylibrum.security.JwtUtil;
import com.sgb.mylibrum.services.AutorService;
import com.sgb.mylibrum.services.ConfiguracaoService;
import com.sgb.mylibrum.services.EditoraService;
import com.sgb.mylibrum.services.EmprestimoService;
import com.sgb.mylibrum.services.EstanteService;
import com.sgb.mylibrum.services.FilialService;
import com.sgb.mylibrum.services.HistoricoMultaService;
import com.sgb.mylibrum.services.ListaTarefaService;
import com.sgb.mylibrum.services.MaterialIsbnService;
import com.sgb.mylibrum.services.MaterialService;
import com.sgb.mylibrum.services.RecuperacaoSenhaService;
import com.sgb.mylibrum.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ControllerMvcCoverageTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AutorService autorService;
    @Mock private ConfiguracaoService configuracaoService;
    @Mock private EditoraService editoraService;
    @Mock private EmprestimoService emprestimoService;
    @Mock private EstanteService estanteService;
    @Mock private FilialService filialService;
    @Mock private HistoricoMultaService historicoMultaService;
    @Mock private ListaTarefaService listaTarefaService;
    @Mock private MaterialService materialService;
    @Mock private MaterialIsbnService materialIsbnService;
    @Mock private RecuperacaoSenhaService recuperacaoSenhaService;
    @Mock private UsuarioService usuarioService;
    @Mock private AutorizacaoService autorizacaoService;

    @Test
    void testaFluxoHttpDeLogin() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@example.test");
        usuario.setSenha("hash");
        usuario.setFuncao(FuncaoUsuario.ADMIN);
        when(usuarioRepository.findByEmail("admin@example.test")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
        when(jwtUtil.generateToken("admin@example.test", FuncaoUsuario.ADMIN)).thenReturn("jwt-teste");

        mvc(new AuthController(usuarioRepository, passwordEncoder, jwtUtil))
                .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@example.test\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-teste"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.funcao").value("ADMIN"));
    }

    @Test
    void testaRotasCrudDeAutorEValidacao() throws Exception {
        AutorResponseDTO dto = new AutorResponseDTO();
        dto.setId(1L);
        dto.setNome("Machado");
        when(autorService.findAll(true)).thenReturn(List.of(dto));
        when(autorService.findById(1L)).thenReturn(dto);
        when(autorService.create(any(AutorRequestDTO.class))).thenReturn(dto);
        when(autorService.update(eq(1L), any(AutorRequestDTO.class))).thenReturn(dto);

        MockMvc mvc = mvc(new AutorController(autorService));
        mvc.perform(get("/api/autores").param("ativo", "true"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].nome").value("Machado"));
        mvc.perform(get("/api/autores/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
        mvc.perform(post("/api/autores").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Machado\"}"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/autores/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Machado atualizado\"}"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/autores/1")).andExpect(status().isNoContent());
        mvc.perform(post("/api/autores").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\" \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testaRotasCrudDeConfiguracao() throws Exception {
        ConfiguracaoResponseDTO dto = new ConfiguracaoResponseDTO();
        dto.setId(2L);
        dto.setChave("limite");
        when(configuracaoService.findAll(false)).thenReturn(List.of(dto));
        when(configuracaoService.findById(2L)).thenReturn(dto);
        when(configuracaoService.create(any(ConfiguracaoRequestDTO.class))).thenReturn(dto);
        when(configuracaoService.update(eq(2L), any(ConfiguracaoRequestDTO.class))).thenReturn(dto);
        MockMvc mvc = mvc(new ConfiguracaoController(configuracaoService));
        String request = "{\"filialId\":1,\"chave\":\"limite\",\"valor\":\"5\"}";

        mvc.perform(get("/api/configuracoes").param("ativo", "false")).andExpect(status().isOk());
        mvc.perform(get("/api/configuracoes/2")).andExpect(status().isOk());
        mvc.perform(post("/api/configuracoes").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/configuracoes/2").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/configuracoes/2")).andExpect(status().isNoContent());
        mvc.perform(post("/api/configuracoes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"filialId\":1,\"chave\":\" \",\"valor\":\"v\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testaRotasCrudDeEditora() throws Exception {
        EditoraResponseDTO dto = new EditoraResponseDTO();
        dto.setId(3L);
        dto.setNome("Editora");
        when(editoraService.findAll(true)).thenReturn(List.of(dto));
        when(editoraService.findById(3L)).thenReturn(dto);
        when(editoraService.create(any(EditoraRequestDTO.class))).thenReturn(dto);
        when(editoraService.update(eq(3L), any(EditoraRequestDTO.class))).thenReturn(dto);
        MockMvc mvc = mvc(new EditoraController(editoraService));

        mvc.perform(get("/api/editoras").param("ativo", "true")).andExpect(status().isOk());
        mvc.perform(get("/api/editoras/3")).andExpect(status().isOk());
        mvc.perform(post("/api/editoras").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Editora\"}"))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/editoras/3").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"Editora\"}"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/editoras/3")).andExpect(status().isNoContent());
    }

    @Test
    void testaRotasDeEmprestimoEAutorizacaoDoProprietario() throws Exception {
        EmprestimoResponseDTO dto = new EmprestimoResponseDTO();
        dto.setId(5L);
        dto.setUsuarioId(9L);
        when(emprestimoService.findAll(true)).thenReturn(List.of(dto));
        when(emprestimoService.findByUsuarioId(9L, false)).thenReturn(List.of(dto));
        when(emprestimoService.findById(5L)).thenReturn(dto);
        when(emprestimoService.create(any(EmprestimoRequestDTO.class))).thenReturn(dto);
        when(emprestimoService.update(eq(5L), any(EmprestimoRequestDTO.class))).thenReturn(dto);
        when(emprestimoService.devolver(5L)).thenReturn(dto);
        when(emprestimoService.renovar(5L)).thenReturn(dto);
        MockMvc mvc = mvc(new EmprestimoController(emprestimoService, autorizacaoService));
        String request = emprestimoJson();

        mvc.perform(get("/api/emprestimos").param("ativo", "true")).andExpect(status().isOk());
        mvc.perform(get("/api/emprestimos/usuarios/9").param("ativo", "false")).andExpect(status().isOk());
        mvc.perform(get("/api/emprestimos/5")).andExpect(status().isOk());
        mvc.perform(post("/api/emprestimos").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/emprestimos/5/devolucao")).andExpect(status().isOk());
        mvc.perform(post("/api/emprestimos/5/renovacao")).andExpect(status().isOk());
        mvc.perform(put("/api/emprestimos/5").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/emprestimos/5")).andExpect(status().isNoContent());
        verify(autorizacaoService, times(2)).exigirProprioOuEquipe(9L);
    }

    @Test
    void testaRotasCrudDeEstante() throws Exception {
        EstanteResponseDTO dto = new EstanteResponseDTO();
        dto.setId(6L);
        when(estanteService.findAll(true)).thenReturn(List.of(dto));
        when(estanteService.findByFilialId(2L, false)).thenReturn(List.of(dto));
        when(estanteService.findById(6L)).thenReturn(dto);
        when(estanteService.create(any(EstanteRequestDTO.class))).thenReturn(dto);
        when(estanteService.update(eq(6L), any(EstanteRequestDTO.class))).thenReturn(dto);
        MockMvc mvc = mvc(new EstanteController(estanteService));
        String request = "{\"filialId\":2,\"nome\":\"A\",\"localizacao\":\"Sala\"}";

        mvc.perform(get("/api/estantes").param("ativo", "true")).andExpect(status().isOk());
        mvc.perform(get("/api/estantes/filiais/2").param("ativo", "false")).andExpect(status().isOk());
        mvc.perform(get("/api/estantes/6")).andExpect(status().isOk());
        mvc.perform(post("/api/estantes").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/estantes/6").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/estantes/6")).andExpect(status().isNoContent());
    }

    @Test
    void testaRotasCrudDeFilial() throws Exception {
        FilialResponseDTO dto = new FilialResponseDTO();
        dto.setId(7L);
        when(filialService.findAll(true)).thenReturn(List.of(dto));
        when(filialService.findById(7L)).thenReturn(dto);
        when(filialService.create(any(FilialRequestDTO.class))).thenReturn(dto);
        when(filialService.update(eq(7L), any(FilialRequestDTO.class))).thenReturn(dto);
        MockMvc mvc = mvc(new FilialController(filialService));
        String request = filialJson();

        mvc.perform(get("/api/filiais").param("ativo", "true")).andExpect(status().isOk());
        mvc.perform(get("/api/filiais/7")).andExpect(status().isOk());
        mvc.perform(post("/api/filiais").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/filiais/7").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/filiais/7")).andExpect(status().isNoContent());
    }

    @Test
    void testaRotasDeHistoricoMulta() throws Exception {
        HistoricoMultaResponseDTO dto = new HistoricoMultaResponseDTO();
        dto.setId(8L);
        dto.setUsuarioId(9L);
        when(historicoMultaService.findAll(StatusMulta.PENDENTE, true)).thenReturn(List.of(dto));
        when(historicoMultaService.findByUsuarioId(9L, false)).thenReturn(List.of(dto));
        when(historicoMultaService.findById(8L)).thenReturn(dto);
        when(historicoMultaService.create(any(HistoricoMultaRequestDTO.class))).thenReturn(dto);
        when(historicoMultaService.update(eq(8L), any(HistoricoMultaRequestDTO.class))).thenReturn(dto);
        when(historicoMultaService.registrarPagamento(8L)).thenReturn(dto);
        MockMvc mvc = mvc(new HistoricoMultaController(historicoMultaService, autorizacaoService));
        String request = "{\"emprestimoId\":1,\"valor\":12.50,\"diasAtraso\":2}";

        mvc.perform(get("/api/historico-multas").param("status", "PENDENTE").param("ativo", "true"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/historico-multas/usuarios/9").param("ativo", "false"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/historico-multas/8")).andExpect(status().isOk());
        mvc.perform(post("/api/historico-multas").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(patch("/api/historico-multas/8/pagamento")).andExpect(status().isOk());
        mvc.perform(put("/api/historico-multas/8").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/historico-multas/8")).andExpect(status().isNoContent());
        verify(autorizacaoService, times(2)).exigirProprioOuEquipe(9L);
    }

    @Test
    void testaRotasDeTarefaEAssociacaoAoUsuarioLogado() throws Exception {
        ListaTarefaResponseDTO dto = new ListaTarefaResponseDTO();
        dto.setId(10L);
        dto.setUsuarioId(9L);
        when(autorizacaoService.funcaoLogada()).thenReturn(FuncaoUsuario.USUARIO);
        when(autorizacaoService.idLogado()).thenReturn(9L);
        when(listaTarefaService.findByGestorId(9L)).thenReturn(List.of(dto));
        when(listaTarefaService.findByGestorId(10L)).thenReturn(List.of(dto));
        when(listaTarefaService.findById(10L)).thenReturn(dto);
        when(listaTarefaService.create(any(ListaTarefaRequestDTO.class))).thenReturn(dto);
        when(listaTarefaService.update(eq(10L), any(ListaTarefaRequestDTO.class))).thenReturn(dto);
        MockMvc mvc = mvc(new ListaTarefaController(listaTarefaService, autorizacaoService));
        String request = "{\"usuarioId\":99,\"descricao\":\"Organizar\",\"status\":\"PENDENTE\"}";

        mvc.perform(get("/api/tarefas").param("ativo", "true")).andExpect(status().isOk());
        mvc.perform(get("/api/tarefas/gestores/10")).andExpect(status().isOk());
        mvc.perform(get("/api/tarefas/10")).andExpect(status().isOk());
        mvc.perform(post("/api/tarefas").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/tarefas/10").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/tarefas/10")).andExpect(status().isNoContent());
        verify(autorizacaoService).exigirProprioOuEquipe(10L);
        verify(autorizacaoService, times(2)).exigirProprio(9L);
    }

    @Test
    void testaRotasDeMaterialECodigosSemConteudo() throws Exception {
        MaterialResponseDTO dto = new MaterialResponseDTO();
        dto.setId(11L);
        dto.setTitulo("Livro");
        when(materialService.create(any(MaterialRequestDTO.class))).thenReturn(dto);
        when(materialService.findAll(any())).thenReturn(List.of(dto));
        when(materialService.findById(11L)).thenReturn(dto);
        when(materialService.findByIsbn("9780000000000")).thenReturn(dto);
        when(materialService.findByTitulo("Livro", true)).thenReturn(List.of(dto));
        when(materialService.findByEditoraId(2L, false)).thenReturn(List.of(dto));
        when(materialService.update(eq(11L), any(MaterialRequestDTO.class))).thenReturn(dto);
        MockMvc mvc = mvc(new MaterialController(materialService));
        String request = "{\"titulo\":\"Livro\",\"editoraId\":2}";

        mvc.perform(post("/api/materiais").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/materiais").param("titulo", "Livro").param("ativo", "todos"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/materiais/11")).andExpect(status().isOk());
        mvc.perform(get("/api/materiais/isbn").param("isbn", "9780000000000")).andExpect(status().isOk());
        mvc.perform(get("/api/materiais/titulo").param("titulo", "Livro").param("ativo", "true"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/materiais/editora").param("editoraId", "2").param("ativo", "false"))
                .andExpect(status().isOk());
        mvc.perform(put("/api/materiais/11").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/materiais/11")).andExpect(status().isNoContent());
        mvc.perform(get("/api/materiais").param("ativo", "invalido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testaConsultaECadastroPorIsbnComStatusCondicional() throws Exception {
        ConsultaIsbnResponseDTO consulta = new ConsultaIsbnResponseDTO(
                "9780000000000", false, null, false, null, false, List.of(), List.of());
        CadastroIsbnResponseDTO cadastrado = new CadastroIsbnResponseDTO(true, "MINIMO", new MaterialResponseDTO());
        when(materialIsbnService.consultar("9780000000000")).thenReturn(consulta);
        when(materialIsbnService.cadastrar(any(CadastroIsbnRequestDTO.class))).thenReturn(cadastrado);
        MockMvc mvc = mvc(new MaterialIsbnController(materialIsbnService));

        mvc.perform(get("/api/materiais/isbn/consulta").param("isbn", "9780000000000"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/materiais/isbn/cadastro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"9780000000000\"}"))
                .andExpect(status().isCreated());

        when(materialIsbnService.cadastrar(any(CadastroIsbnRequestDTO.class)))
                .thenReturn(new CadastroIsbnResponseDTO(false, "LOCAL", new MaterialResponseDTO()));
        mvc.perform(post("/api/materiais/isbn/cadastro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\":\"9780000000000\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testaFluxosDeRecuperacaoDeSenha() throws Exception {
        MockMvc mvc = mvc(new RecuperacaoSenhaController(recuperacaoSenhaService));

        mvc.perform(post("/auth/esqueci-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"pessoa@example.test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
        mvc.perform(post("/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"token-teste\",\"novaSenha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Senha redefinida com sucesso"));
        mvc.perform(post("/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\" \",\"novaSenha\":\"123\"}"))
                .andExpect(status().isBadRequest());
        verify(recuperacaoSenhaService).solicitar("pessoa@example.test");
        verify(recuperacaoSenhaService).redefinir("token-teste", "senha123");
    }

    @Test
    void testaRotasDeUsuarioComPaginacaoEBloqueio() throws Exception {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(12L);
        dto.setFuncao(FuncaoUsuario.USUARIO);
        when(usuarioService.findAll(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));
        when(usuarioService.findById(12L)).thenReturn(dto);
        when(usuarioService.create(any(UsuarioRequestDTO.class))).thenReturn(dto);
        when(usuarioService.update(eq(12L), any(UsuarioRequestDTO.class))).thenReturn(dto);
        when(usuarioService.atualizarBloqueio(12L, true)).thenReturn(dto);
        when(autorizacaoService.funcaoLogada()).thenReturn(FuncaoUsuario.ADMIN);
        MockMvc mvc = mvc(new UsuarioController(usuarioService, autorizacaoService));
        String request = usuarioJson();

        mvc.perform(get("/api/usuarios").param("nome", "Leitor").param("page", "0").param("size", "10"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/usuarios/12")).andExpect(status().isOk());
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mvc.perform(put("/api/usuarios/12").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isOk());
        mvc.perform(patch("/api/usuarios/12/bloqueio").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bloqueado\":true}"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/usuarios/12")).andExpect(status().isNoContent());
        verify(autorizacaoService).exigirProprioOuEquipe(12L);
        verify(autorizacaoService, times(2))
                .exigirPodeGerenciarConta(12L, FuncaoUsuario.USUARIO, false);
    }

    private MockMvc mvc(Object controller) {
        return MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private String emprestimoJson() {
        return """
                {"usuarioId":9,"exemplarId":3,"usuarioResponsavelId":2,"dataDevolucaoPrevista":"2026-12-01"}
                """;
    }

    private String filialJson() {
        return """
                {"nomeFantasia":"Filial","razaoSocial":"Filial Ltda","cnpj":"12345678000199",
                 "endereco":"Rua A","bairro":"Centro","cidade":"São Paulo","estado":"SP","cep":"01000000"}
                """;
    }

    private String usuarioJson() {
        return """
                {"nome":"Leitor","email":"leitor@example.test","senha":"senha123","filialId":1}
                """;
    }
}
