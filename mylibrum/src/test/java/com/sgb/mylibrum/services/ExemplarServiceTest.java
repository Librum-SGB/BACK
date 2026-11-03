package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgb.mylibrum.dtos.mapper.ExemplarMapper;
import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;

@ExtendWith(MockitoExtension.class)
class ExemplarServiceTest {

    @Mock
    private ExemplarRepository repository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private ExemplarMapper mapper;

    private ExemplarService service;

    @BeforeEach
    void setUp() {
        service = new ExemplarService(repository, materialRepository, mapper);
    }

    @Test
    void deveCriarExemplarEAtualizarQuantidadeDoMaterial() {
        ExemplarRequestDTO request = new ExemplarRequestDTO();
        Material material = new Material();
        material.setId(11L);
        Exemplar exemplar = new Exemplar();
        exemplar.setMaterial(material);
        Material persistido = new Material();
        persistido.setId(11L);
        when(mapper.toEntity(request)).thenReturn(exemplar);
        when(repository.save(exemplar)).thenReturn(exemplar);
        when(materialRepository.findById(11L)).thenReturn(Optional.of(persistido));
        when(repository.findByMaterialId(11L)).thenReturn(List.of(exemplar, new Exemplar()));
        when(mapper.toResponseDTO(exemplar)).thenReturn(new ExemplarResponseDTO());

        service.create(request);

        assertEquals(2, persistido.getQuantidadeExemplares());
        verify(materialRepository).save(persistido);
    }

    @Test
    void deveExcluirExemplarERecalcularQuantidade() {
        Material material = new Material();
        material.setId(11L);
        Exemplar exemplar = new Exemplar();
        exemplar.setMaterial(material);
        Material persistido = new Material();
        persistido.setId(11L);
        when(repository.findById(8L)).thenReturn(Optional.of(exemplar));
        when(materialRepository.findById(11L)).thenReturn(Optional.of(persistido));
        when(repository.findByMaterialId(11L)).thenReturn(List.of());

        service.delete(8L);

        assertTrue(exemplar.getExcluido());
        assertEquals(0, persistido.getQuantidadeExemplares());
        verify(repository).save(exemplar);
        verify(materialRepository).save(persistido);
    }

    @Test
    void naoDeveBuscarMaterialQuandoExemplarNaoTemMaterialPersistido() {
        Exemplar exemplar = new Exemplar();
        exemplar.setMaterial(new Material());
        when(repository.findById(8L)).thenReturn(Optional.of(exemplar));

        service.delete(8L);

        verify(materialRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
        verify(repository).save(exemplar);
    }

    @Test
    void naoDeveFalharNaAtualizacaoDaContagemQuandoExemplarNaoTemMaterial() {
        ExemplarRequestDTO request = new ExemplarRequestDTO();
        Exemplar exemplar = new Exemplar();
        when(mapper.toEntity(request)).thenReturn(exemplar);
        when(repository.save(exemplar)).thenReturn(exemplar);
        when(mapper.toResponseDTO(exemplar)).thenReturn(new ExemplarResponseDTO());

        service.create(request);

        verify(materialRepository, never()).findById(org.mockito.ArgumentMatchers.anyLong());
    }
}
