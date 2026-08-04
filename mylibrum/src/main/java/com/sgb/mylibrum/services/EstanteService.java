package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.EstanteDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.repositories.EstanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstanteService {

    private final EstanteRepository repository;

    @Transactional(readOnly = true)
    public List<EstanteDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstanteDTO findById(Long id) {
        Estante entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public EstanteDTO create(EstanteDTO dto) {
        Estante entity = new Estante();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public EstanteDTO update(Long id, EstanteDTO dto) {
        Estante entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private EstanteDTO toDTO(Estante entity) {
        EstanteDTO dto = new EstanteDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}