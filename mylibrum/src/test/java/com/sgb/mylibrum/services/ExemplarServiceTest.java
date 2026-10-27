package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.mapper.ExemplarMapper;
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
import static org.mockito.ArgumentMatchers.any;
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
