package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.sgb.mylibrum.dtos.mapper.AutorMapper;
import com.sgb.mylibrum.dtos.request.AutorRequestDTO;
import com.sgb.mylibrum.dtos.response.AutorResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.AutorRepository;

@ExtendWith(MockitoExtension.class)
class AutorServiceTest {

    @Mock
    private AutorRepository repository;
    @Mock
    private AutorMapper mapper;

    private AutorService service;

    @BeforeEach
    void setUp() {
        service = new AutorService(repository, mapper);
    }

    @Test
    void deveCriarAutorEConverterResposta() {
        AutorRequestDTO request = new AutorRequestDTO();
        Autor autor = new Autor();
        AutorResponseDTO response = new AutorResponseDTO();
        when(mapper.toEntity(request)).thenReturn(autor);
        when(repository.save(autor)).thenReturn(autor);
        when(mapper.toResponseDTO(autor)).thenReturn(response);

        assertEquals(response, service.create(request));

        verify(repository).save(autor);
    }

    @Test
    void deveAtualizarAutorExistente() {
        AutorRequestDTO request = new AutorRequestDTO();
        Autor autor = new Autor();
        autor.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(autor));
        when(repository.save(autor)).thenReturn(autor);
        when(mapper.toResponseDTO(autor)).thenReturn(new AutorResponseDTO());

        service.update(5L, request);

        verify(mapper).updateEntity(request, autor);
        verify(repository).save(autor);
    }

    @Test
    void deveFalharAoAtualizarAutorInexistente() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(9L, new AutorRequestDTO()));
    }

    @Test
    void deveExcluirAutorDeFormaLogica() {
        Autor autor = new Autor();
        when(repository.existsById(3L)).thenReturn(true);
        when(repository.findById(3L)).thenReturn(Optional.of(autor));

        service.delete(3L);

        org.junit.jupiter.api.Assertions.assertTrue(autor.getExcluido());
        verify(repository).save(autor);
    }

    @Test
    void deveListarAutoresUsandoFiltroAtivo() {
        when(repository.findAll(any(Specification.class))).thenReturn(java.util.List.of());

        assertEquals(0, service.findAll(false).size());

        verify(repository).findAll(any(Specification.class));
    }
}
