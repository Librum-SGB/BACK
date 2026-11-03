package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgb.mylibrum.dtos.mapper.EstanteMapper;
import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.exceptions.BusinessException;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EstanteRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.FilialRepository;

@ExtendWith(MockitoExtension.class)
class EstanteServiceTest {

    @Mock
    private EstanteRepository repository;
    @Mock
    private FilialRepository filialRepository;
    @Mock
    private ExemplarRepository exemplarRepository;
    @Mock
    private EstanteMapper mapper;

    private EstanteService service;

    @BeforeEach
    void setUp() {
        service = new EstanteService(repository, filialRepository, exemplarRepository, mapper);
    }

    @Test
    void deveCriarEstanteAssociadaAFilial() {
        EstanteRequestDTO request = new EstanteRequestDTO();
        request.setFilialId(10L);
        Estante estante = new Estante();
        Filial filial = new Filial();
        filial.setId(10L);
        when(mapper.toEntity(request)).thenReturn(estante);
        when(filialRepository.findById(10L)).thenReturn(Optional.of(filial));
        when(repository.save(estante)).thenReturn(estante);
        when(mapper.toResponseDTO(estante)).thenReturn(new EstanteResponseDTO());

        service.create(request);

        assertEquals(filial, estante.getFilial());
        verify(repository).save(estante);
    }

    @Test
    void deveFalharAoCriarEstanteComFilialInexistente() {
        EstanteRequestDTO request = new EstanteRequestDTO();
        request.setFilialId(10L);
        when(mapper.toEntity(request)).thenReturn(new Estante());
        when(filialRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.create(request));

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any(Estante.class));
    }

    @Test
    void deveImpedirExclusaoDeEstanteComExemplares() {
        when(exemplarRepository.existsByEstanteId(6L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.delete(6L));

        verify(repository, never()).findById(6L);
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any(Estante.class));
    }

    @Test
    void deveExcluirEstanteVaziaDeFormaLogica() {
        Estante estante = new Estante();
        when(exemplarRepository.existsByEstanteId(6L)).thenReturn(false);
        when(repository.findById(6L)).thenReturn(Optional.of(estante));

        service.delete(6L);

        assertTrue(estante.getExcluido());
        verify(repository).save(estante);
    }

    @Test
    void deveValidarFilialAntesDeListarEstantes() {
        when(filialRepository.existsById(10L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findByFilialId(10L, true));

        verify(repository, never()).findAll(org.mockito.ArgumentMatchers
                .<org.springframework.data.jpa.domain.Specification<Estante>>any());
    }
}
