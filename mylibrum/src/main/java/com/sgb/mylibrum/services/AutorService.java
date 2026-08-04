package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.AutorDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.repositories.AutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository repository;

    @Transactional(readOnly = true)
    public List<AutorDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AutorDTO findById(Long id) {
        Autor entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public AutorDTO create(AutorDTO dto) {
        Autor entity = new Autor();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public AutorDTO update(Long id, AutorDTO dto) {
        Autor entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private AutorDTO toDTO(Autor entity) {
        AutorDTO dto = new AutorDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}