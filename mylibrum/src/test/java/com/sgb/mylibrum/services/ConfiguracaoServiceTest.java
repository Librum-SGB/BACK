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

import com.sgb.mylibrum.dtos.mapper.ConfiguracaoMapper;
import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;

@ExtendWith(MockitoExtension.class)
class ConfiguracaoServiceTest {

    @Mock
    private ConfiguracaoRepository repository;
    @Mock
    private ConfiguracaoMapper mapper;

    private ConfiguracaoService service;

    @BeforeEach
    void setUp() {
        service = new ConfiguracaoService(repository, mapper);
    }

    @Test
    void deveCriarConfiguracao() {
        ConfiguracaoRequestDTO request = new ConfiguracaoRequestDTO();
        Configuracao configuracao = new Configuracao();
        ConfiguracaoResponseDTO response = new ConfiguracaoResponseDTO();
        when(mapper.toEntity(request)).thenReturn(configuracao);
        when(repository.save(configuracao)).thenReturn(configuracao);
        when(mapper.toResponseDTO(configuracao)).thenReturn(response);

        assertEquals(response, service.create(request));
    }

    @Test
    void deveAtualizarConfiguracaoExistente() {
        ConfiguracaoRequestDTO request = new ConfiguracaoRequestDTO();
        Configuracao configuracao = new Configuracao();
        when(repository.findById(7L)).thenReturn(Optional.of(configuracao));
        when(repository.save(configuracao)).thenReturn(configuracao);
        when(mapper.toResponseDTO(configuracao)).thenReturn(new ConfiguracaoResponseDTO());

        service.update(7L, request);

        verify(mapper).updateEntity(request, configuracao);
        verify(repository).save(configuracao);
    }

    @Test
    void deveLancarExcecaoQuandoConfiguracaoNaoExiste() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(7L));
    }

    @Test
    void deveExcluirConfiguracaoDeFormaLogica() {
        Configuracao configuracao = new Configuracao();
        when(repository.findById(7L)).thenReturn(Optional.of(configuracao));

        service.delete(7L);

        assertTrue(configuracao.getExcluido());
        verify(repository).save(configuracao);
    }
}
