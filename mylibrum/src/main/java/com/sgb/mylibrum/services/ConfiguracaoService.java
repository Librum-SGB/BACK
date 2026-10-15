package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.mapper.ConfiguracaoMapper;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository repository;
    private final ConfiguracaoMapper mapper;

    @Transactional(readOnly = true)
    public List<ConfiguracaoResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public ConfiguracaoResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada")));
    }

    @Transactional
    public ConfiguracaoResponseDTO create(ConfiguracaoRequestDTO dto) {
        return mapper.toResponseDTO(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public ConfiguracaoResponseDTO update(Long id, ConfiguracaoRequestDTO dto) {
        Configuracao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada"));
        mapper.updateEntity(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Configuracao configuracao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada"));
        configuracao.setExcluido(true);
        repository.save(configuracao);
    }
}