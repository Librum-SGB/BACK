package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.GestorDTO;
import com.sgb.mylibrum.entities.Gestor;
import com.sgb.mylibrum.repositories.GestorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestorService {

    private final GestorRepository repository;

    @Transactional(readOnly = true)
    public List<GestorDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GestorDTO findById(Long id) {
        Gestor entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gestor não encontrado com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public GestorDTO create(GestorDTO dto) {
        Gestor entity = new Gestor();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public GestorDTO update(Long id, GestorDTO dto) {
        Gestor entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gestor não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private GestorDTO toDTO(Gestor entity) {
        GestorDTO dto = new GestorDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getFilial() != null) dto.setFilialId(entity.getFilial().getId());
        return dto;
    }
}