package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.GeneroDTO;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.repositories.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneroService {

    private final GeneroRepository repository;

    @Transactional(readOnly = true)
    public List<GeneroDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GeneroDTO findById(Long id) {
        Genero entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gênero não encontrado com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public GeneroDTO create(GeneroDTO dto) {
        Genero entity = new Genero();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public GeneroDTO update(Long id, GeneroDTO dto) {
        Genero entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gênero não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private GeneroDTO toDTO(Genero entity) {
        GeneroDTO dto = new GeneroDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}