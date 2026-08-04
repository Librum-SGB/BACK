package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.ConfiguracaoDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoRepository repository;

    @Transactional(readOnly = true)
    public List<ConfiguracaoDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConfiguracaoDTO findById(Long id) {
        Configuracao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public ConfiguracaoDTO create(ConfiguracaoDTO dto) {
        Configuracao entity = new Configuracao();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        // TODO: Buscar a filial pelo dto.getFilialId() e setar na entity (entity.setFilial(filial))
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public ConfiguracaoDTO update(Long id, ConfiguracaoDTO dto) {
        Configuracao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ConfiguracaoDTO toDTO(Configuracao entity) {
        ConfiguracaoDTO dto = new ConfiguracaoDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getFilial() != null) dto.setFilialId(entity.getFilial().getId());
        return dto;
    }
}