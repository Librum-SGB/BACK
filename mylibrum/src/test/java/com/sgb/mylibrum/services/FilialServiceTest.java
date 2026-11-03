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

import com.sgb.mylibrum.dtos.mapper.FilialMapper;
import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.repositories.FilialRepository;

@ExtendWith(MockitoExtension.class)
class FilialServiceTest {

    @Mock
    private FilialRepository repository;
    @Mock
    private FilialMapper mapper;

    private FilialService service;

    @BeforeEach
    void setUp() {
        service = new FilialService(repository, mapper);
    }

    @Test
    void deveCriarFilial() {
        FilialRequestDTO request = new FilialRequestDTO();
        Filial filial = new Filial();
        FilialResponseDTO response = new FilialResponseDTO();
        when(mapper.toEntity(request)).thenReturn(filial);
        when(repository.save(filial)).thenReturn(filial);
        when(mapper.toResponseDTO(filial)).thenReturn(response);

        assertEquals(response, service.create(request));
    }

    @Test
    void deveAtualizarFilialExistente() {
        FilialRequestDTO request = new FilialRequestDTO();
        Filial filial = new Filial();
        when(repository.findById(2L)).thenReturn(Optional.of(filial));
        when(repository.save(filial)).thenReturn(filial);
        when(mapper.toResponseDTO(filial)).thenReturn(new FilialResponseDTO());

        service.update(2L, request);

        verify(mapper).updateEntity(request, filial);
        verify(repository).save(filial);
    }

    @Test
    void deveRetornarErroQuandoFilialNaoExiste() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(2L));
    }

    @Test
    void deveExcluirFilialDeFormaLogica() {
        Filial filial = new Filial();
        when(repository.findById(2L)).thenReturn(Optional.of(filial));

        service.delete(2L);

        assertTrue(filial.getExcluido());
        verify(repository).save(filial);
    }
}
