package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.mapper.ConfiguracaoMapper;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository repository;
    private final ConfiguracaoMapper mapper;

    @Transactional(readOnly = true)
    public List<ConfiguracaoResponseDTO> findAll(Boolean ativo) {
        log.debug("Buscando configuracoes: ativo={}", ativo);
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public ConfiguracaoResponseDTO findById(Long id) {
        log.debug("Buscando configuracao: id={}", id);
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Configuracao nao encontrada: id={}", id);
                    return new RuntimeException("Configura??o n?o encontrada");
                }));
    }

    @Transactional
    public ConfiguracaoResponseDTO create(ConfiguracaoRequestDTO dto) {
        Configuracao configuracao = repository.save(mapper.toEntity(dto));
        log.info("Configuracao criada: id={}", configuracao.getId());
        return mapper.toResponseDTO(configuracao);
    }

    @Transactional
    public ConfiguracaoResponseDTO update(Long id, ConfiguracaoRequestDTO dto) {
        Configuracao entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Configuracao nao encontrada para atualizacao: id={}", id);
                    return new RuntimeException("Configura??o n?o encontrada");
                });
        mapper.updateEntity(dto, entity);
        Configuracao atualizada = repository.save(entity);
        log.info("Configuracao atualizada: id={}", atualizada.getId());
        return mapper.toResponseDTO(atualizada);
    }

    @Transactional
    public void delete(Long id) {
        Configuracao configuracao = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Configuracao nao encontrada para exclusao: id={}", id);
                    return new RuntimeException("Configura??o n?o encontrada");
                });
        configuracao.setExcluido(true);
        repository.save(configuracao);
        log.info("Configuracao excluida: id={}", configuracao.getId());
    }
}