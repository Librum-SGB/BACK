package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgb.mylibrum.dtos.mapper.EditoraMapper;
import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EditoraRepository;

@ExtendWith(MockitoExtension.class)
class EditoraServiceTest {

    @Mock
    private EditoraRepository repository;
    @Mock
    private EditoraMapper mapper;

    private EditoraService service;

    @BeforeEach
    void setUp() {
        service = new EditoraService(repository, mapper);
    }

    @Test
    void deveCriarEditora() {
        EditoraRequestDTO request = new EditoraRequestDTO();
        Editora editora = new Editora();
        EditoraResponseDTO response = new EditoraResponseDTO();
        when(mapper.toEntity(request)).thenReturn(editora);
        when(repository.save(editora)).thenReturn(editora);
        when(mapper.toResponseDTO(editora)).thenReturn(response);

        assertEquals(response, service.create(request));
    }

    @Test
    void deveAtualizarEditoraExistente() {
        EditoraRequestDTO request = new EditoraRequestDTO();
        Editora editora = new Editora();
        when(repository.findById(4L)).thenReturn(Optional.of(editora));
        when(repository.save(editora)).thenReturn(editora);
        when(mapper.toResponseDTO(editora)).thenReturn(new EditoraResponseDTO());

        service.update(4L, request);

        verify(mapper).updateEntity(request, editora);
        verify(repository).save(editora);
    }

    @Test
    void deveLancarExcecaoQuandoEditoraNaoExiste() {
        when(repository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(4L));
    }

    @Test
    void deveExcluirEditoraDeFormaLogica() {
        Editora editora = new Editora();
        when(repository.findById(4L)).thenReturn(Optional.of(editora));

        service.delete(4L);

        assertTrue(editora.getExcluido());
        verify(repository).save(editora);
    }
}
