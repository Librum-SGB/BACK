package com.sgb.mylibrum.exceptions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StubController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornar409ParaRecursoDuplicado() throws Exception {
        mockMvc.perform(get("/duplicado"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("duplicado"))
                .andExpect(jsonPath("$.path").value("/duplicado"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

    @Test
    void deveRetornar422ParaRegraDeNegocio() throws Exception {
        mockMvc.perform(get("/negocio"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("regra"));
    }

    @Test
    void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
        mockMvc.perform(get("/credenciais"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }

    @Test
    void deveRetornar400ParaTipoDeParametroInvalido() throws Exception {
        mockMvc.perform(get("/item/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Valor inválido para o parâmetro 'id'"));
    }

    @Test
    void deveRetornar405ParaMetodoNaoPermitido() throws Exception {
        mockMvc.perform(post("/duplicado"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void deveRetornar500SemVazarDetalhesParaErroInesperado() throws Exception {
        mockMvc.perform(get("/erro"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }

    @RestController
    static class StubController {
        @GetMapping("/duplicado")
        String duplicado() {
            throw new DuplicateResourceException("duplicado");
        }

        @GetMapping("/negocio")
        String negocio() {
            throw new BusinessException("regra");
        }

        @GetMapping("/credenciais")
        String credenciais() {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        @GetMapping("/item/{id}")
        String item(@PathVariable Long id) {
            return String.valueOf(id);
        }

        @GetMapping("/erro")
        String erro() {
            throw new IllegalStateException("segredo interno");
        }
    }
}
