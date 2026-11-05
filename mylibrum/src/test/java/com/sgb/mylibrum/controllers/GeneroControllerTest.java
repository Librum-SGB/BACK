package com.sgb.mylibrum.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.dtos.response.GeneroResponseDTO;
import com.sgb.mylibrum.exceptions.GlobalExceptionHandler;
import com.sgb.mylibrum.services.GeneroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class GeneroControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private GeneroService service;

    @InjectMocks
    private GeneroController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveListarGenerosComFiltroDeAtivo() throws Exception {
        GeneroResponseDTO genero = new GeneroResponseDTO();
        genero.setId(1L);
        genero.setNome("Romance");
        when(service.findAll(false)).thenReturn(List.of(genero));

        mockMvc.perform(get("/api/generos").param("ativo", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Romance"));

        verify(service).findAll(false);
    }

    @Test
    void deveRetornarGeneroPorId() throws Exception {
        GeneroResponseDTO genero = new GeneroResponseDTO();
        genero.setId(7L);
        genero.setNome("Ficção");
        when(service.findById(7L)).thenReturn(genero);

        mockMvc.perform(get("/api/generos/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.nome").value("Ficção"));
    }

    @Test
    void deveRetornar404QuandoGeneroNaoExiste() throws Exception {
        when(service.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/generos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void deveCriarGeneroValido() throws Exception {
        GeneroResponseDTO genero = new GeneroResponseDTO();
        genero.setId(3L);
        genero.setNome("História");
        when(service.create(any(GeneroRequestDTO.class))).thenReturn(genero);

        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest("História", "Narrativas históricas")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nome").value("História"));

        verify(service).create(any(GeneroRequestDTO.class));
    }

    @Test
    void deveRejeitarCadastroComNomeEmBranco() throws Exception {
        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest("  ", "Descrição")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados inválidos"))
                .andExpect(jsonPath("$.fieldErrors.nome").exists());
    }

    @Test
    void deveRejeitarCadastroComNomeMaiorQueCemCaracteres() throws Exception {
        String nome = "a".repeat(101);

        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest(nome, null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.nome").exists());
    }

    @Test
    void deveAtualizarGeneroExistente() throws Exception {
        GeneroResponseDTO genero = new GeneroResponseDTO();
        genero.setId(4L);
        genero.setNome("Poesia");
        when(service.update(eq(4L), any(GeneroRequestDTO.class))).thenReturn(genero);

        mockMvc.perform(put("/api/generos/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest("Poesia", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.nome").value("Poesia"));
    }

    @Test
    void deveRetornar404AoAtualizarGeneroInexistente() throws Exception {
        when(service.update(eq(404L), any(GeneroRequestDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/generos/404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest("Gênero", null)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404AoDesativarAtivarOuExcluirGeneroInexistente() throws Exception {
        when(service.desativar(404L)).thenReturn(false);
        when(service.ativar(404L)).thenReturn(false);
        when(service.delete(404L)).thenReturn(false);

        mockMvc.perform(put("/api/generos/desativar/404"))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/generos/ativar/404"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/generos/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar204AoDesativarAtivarOuExcluirGenero() throws Exception {
        when(service.desativar(4L)).thenReturn(true);
        when(service.ativar(4L)).thenReturn(true);
        when(service.delete(4L)).thenReturn(true);

        mockMvc.perform(put("/api/generos/desativar/4"))
                .andExpect(status().isNoContent());
        mockMvc.perform(put("/api/generos/ativar/4"))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/generos/4"))
                .andExpect(status().isNoContent());
    }

    private String jsonRequest(String nome, String descricao) throws Exception {
        GeneroRequestDTO request = new GeneroRequestDTO();
        request.setNome(nome);
        request.setDescricao(descricao);
        return objectMapper.writeValueAsString(request);
    }
}
