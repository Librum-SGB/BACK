package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.sgb.mylibrum.dtos.mapper.MaterialMapper;
import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.specifications.MaterialFiltro;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository repository;
    @Mock
    private MaterialMapper mapper;

    private MaterialService service;

    @BeforeEach
    void setUp() {
        service = new MaterialService(repository, mapper);
    }

    @Test
    void deveCriarMaterial() {
        MaterialRequestDTO request = new MaterialRequestDTO();
        Material material = new Material();
        MaterialResponseDTO response = new MaterialResponseDTO();
        when(mapper.toEntity(request)).thenReturn(material);
        when(repository.save(material)).thenReturn(material);
        when(mapper.toResponseDTO(material)).thenReturn(response);

        assertEquals(response, service.create(request));
    }

    @Test
    void deveAtualizarMaterialExistente() {
        MaterialRequestDTO request = new MaterialRequestDTO();
        Material material = new Material();
        when(repository.findById(12L)).thenReturn(Optional.of(material));
        when(repository.save(material)).thenReturn(material);
        when(mapper.toResponseDTO(material)).thenReturn(new MaterialResponseDTO());

        service.update(12L, request);

        verify(mapper).updateEntity(request, material);
        verify(repository).save(material);
    }

    @Test
    void deveLancarExcecaoAoBuscarIsbnInexistente() {
        when(repository.findByIsbn("9780000000000")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findByIsbn("9780000000000"));
    }

    @Test
    void deveExcluirMaterialDeFormaLogica() {
        Material material = new Material();
        when(repository.findById(12L)).thenReturn(Optional.of(material));

        service.delete(12L);

        assertTrue(material.getExcluido());
        verify(repository).save(material);
    }

    @Test
    void devePesquisarMateriaisComFiltro() {
        when(repository.findAll(any(Specification.class))).thenReturn(java.util.List.of());

        assertTrue(service.findAll(new MaterialFiltro(null, null, null, null, null,
                null, null, null, null, true)).isEmpty());

        verify(repository).findAll(any(Specification.class));
    }
}
