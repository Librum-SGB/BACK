package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.entities.Filial;
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
    public List<ConfiguracaoResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConfiguracaoResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada")));
    }

    @Transactional
    public ConfiguracaoResponseDTO create(ConfiguracaoRequestDTO dto) {
        Configuracao entity = new Configuracao();
        BeanUtils.copyProperties(dto, entity);
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public ConfiguracaoResponseDTO update(Long id, ConfiguracaoRequestDTO dto) {
        Configuracao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    private void setRelacionamentos(ConfiguracaoRequestDTO dto, Configuracao entity) {
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            entity.setFilial(filial);
        }
    }

    private ConfiguracaoResponseDTO toResponseDTO(Configuracao entity) {
        ConfiguracaoResponseDTO dto = new ConfiguracaoResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getFilial() != null) dto.setFilialId(entity.getFilial().getId());
        return dto;
    }
}