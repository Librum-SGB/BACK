package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.AutorRequestDTO;
import com.sgb.mylibrum.dtos.response.AutorResponseDTO;
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
    public List<AutorResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AutorResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com id: " + id)));
    }

    @Transactional
    public AutorResponseDTO create(AutorRequestDTO dto) {
        Autor entity = new Autor();
        BeanUtils.copyProperties(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public AutorResponseDTO update(Long id, AutorRequestDTO dto) {
        Autor entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private AutorResponseDTO toResponseDTO(Autor entity) {
        AutorResponseDTO dto = new AutorResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}