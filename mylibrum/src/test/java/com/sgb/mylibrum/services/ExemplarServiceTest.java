package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.mapper.ExemplarMapper;
import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExemplarServiceTest {

    @Mock
    private ExemplarRepository repository;
    @Mock
    private MaterialRepository materialRepository;
    @InjectMocks
    private ExemplarService service;

    @Test
    void createRejectsMaterialAlreadyInAShelf() {
        ExemplarRequestDTO request = new ExemplarRequestDTO();
        request.setMaterialId(8L);
        request.setFilialId(2L);
        request.setEstanteId(3L);
        request.setCodigoBarras("BARRA-8");
        when(materialRepository.findByIdForUpdate(8L)).thenReturn(Optional.of(new Material()));
        when(repository.existsByMaterialInActiveShelf(8L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.create(request));

        verify(repository, never()).save(any(Exemplar.class));
    }

    @Test
    void updateRejectsMaterialInAnotherShelf() {
        Exemplar exemplar = new Exemplar();
        exemplar.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(exemplar));
        when(materialRepository.findByIdForUpdate(8L)).thenReturn(Optional.of(new Material()));
        when(repository.existsByMaterialInAnotherActiveShelf(8L, 5L)).thenReturn(true);

        ExemplarRequestDTO request = new ExemplarRequestDTO();
        request.setMaterialId(8L);
        request.setFilialId(2L);
        request.setEstanteId(3L);
        request.setCodigoBarras("BARRA-8");

        assertThrows(IllegalStateException.class, () -> service.update(5L, request));

        verify(repository, never()).save(any(Exemplar.class));
    }

    @Test
    void detachPreservesExemplarAndMaterialButClearsShelfLocation() {
        Exemplar exemplar = new Exemplar();
        exemplar.setId(5L);
        exemplar.setCodigoBarras("BARRA-5");
        Material material = new Material();
        material.setId(8L);
        exemplar.setMaterial(material);
        exemplar.setEstante(new Estante());
        exemplar.setPrateleira("Ficção");
        exemplar.setPosicao("12");
        when(repository.findById(5L)).thenReturn(Optional.of(exemplar));
        when(repository.save(any(Exemplar.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.detachFromEstante(5L);

        assertNull(exemplar.getEstante());
        assertNull(exemplar.getPrateleira());
        assertNull(exemplar.getPosicao());
        org.junit.jupiter.api.Assertions.assertEquals("BARRA-5", exemplar.getCodigoBarras());
        org.junit.jupiter.api.Assertions.assertSame(material, exemplar.getMaterial());
        verify(repository).save(exemplar);
    }
}
