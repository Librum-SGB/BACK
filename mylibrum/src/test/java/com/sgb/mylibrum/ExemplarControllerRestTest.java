package com.sgb.mylibrum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgb.mylibrum.controllers.ExemplarController;
import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.exceptions.GlobalExceptionHandler;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.services.ExemplarService;

@ExtendWith(MockitoExtension.class)
class ExemplarControllerRestTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ExemplarService exemplarService;

    @InjectMocks
    private ExemplarController exemplarController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(exemplarController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn404WhenExemplarNotFoundById() throws Exception {
        when(exemplarService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Exemplar não encontrado"));

        mockMvc.perform(get("/api/exemplares/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn201WhenCreatingExemplar() throws Exception {
        ExemplarRequestDTO request = new ExemplarRequestDTO();
        request.setMaterialId(10L);
        request.setFilialId(20L);
        request.setEstanteId(30L);
        request.setCodigoBarras("ABC-123");

        ExemplarResponseDTO response = new ExemplarResponseDTO();
        response.setId(1L);
        response.setMaterialId(10L);
        response.setFilialId(20L);
        response.setEstanteId(30L);
        response.setCodigoBarras("ABC-123");

        when(exemplarService.create(any(ExemplarRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/exemplares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
        ExemplarRequestDTO request = new ExemplarRequestDTO();
        request.setMaterialId(null);
        request.setFilialId(2L);
        request.setEstanteId(3L);
        request.setCodigoBarras(" ");

        mockMvc.perform(post("/api/exemplares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingExemplar() throws Exception {
        ExemplarRequestDTO request = new ExemplarRequestDTO();
        request.setMaterialId(10L);
        request.setFilialId(20L);
        request.setEstanteId(30L);
        request.setCodigoBarras("ABC-123");

        when(exemplarService.update(eq(999L), any(ExemplarRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Exemplar não encontrado"));

        mockMvc.perform(put("/api/exemplares/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingExemplar() throws Exception {
        doThrow(new ResourceNotFoundException("Exemplar não encontrado"))
                .when(exemplarService).delete(999L);

        mockMvc.perform(delete("/api/exemplares/999"))
                .andExpect(status().isNotFound());
    }
}
