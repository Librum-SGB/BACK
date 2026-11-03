package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sgb.mylibrum.dtos.mapper.GeneroMapper;
import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.repositories.GeneroRepository;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {

    @Mock
    private GeneroRepository repository;
    @Mock
    private GeneroMapper mapper;

    private GeneroService service;

    @BeforeEach
    void setUp() {
        service = new GeneroService(repository, mapper);
    }

    @Test
    void deveRetornarNuloQuandoGeneroNaoExisteOuEstaInativo() {
        when(repository.findByIdAndAtivoTrueAndExcluidoFalse(2L)).thenReturn(Optional.empty());

        assertNull(service.findById(2L));
    }

    @Test
    void deveRetornarNuloAoAtualizarGeneroInexistente() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertNull(service.update(2L, new GeneroRequestDTO()));
    }

    @Test
    void deveDesativarGeneroExistente() {
        Genero genero = new Genero();
        when(repository.findById(2L)).thenReturn(Optional.of(genero));

        assertTrue(service.desativar(2L));
        assertFalse(genero.getAtivo());
    }

    @Test
    void deveReativarGeneroInativo() {
        Genero genero = new Genero();
        genero.setAtivo(false);
        when(repository.findById(2L)).thenReturn(Optional.of(genero));

        assertTrue(service.ativar(2L));
        assertTrue(genero.getAtivo());
    }

    @Test
    void deveRetornarFalsoAoAtivarGeneroInexistente() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertFalse(service.ativar(2L));
    }

    @Test
    void deveExcluirGeneroDesativandoEMarcandoComoExcluido() {
        Genero genero = new Genero();
        when(repository.findById(2L)).thenReturn(Optional.of(genero));

        assertTrue(service.delete(2L));

        assertFalse(genero.getAtivo());
        assertTrue(genero.getExcluido());
        verify(repository).save(genero);
    }

    @Test
    void deveRetornarFalsoAoExcluirGeneroInexistente() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertFalse(service.delete(2L));
    }
}
