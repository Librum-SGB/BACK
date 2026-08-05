package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.dtos.response.GeneroResponseDTO;
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
    public List<GeneroResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GeneroResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gênero não encontrado")));
    }

    @Transactional
    public GeneroResponseDTO create(GeneroRequestDTO dto) {
        Genero entity = new Genero();
        BeanUtils.copyProperties(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public GeneroResponseDTO update(Long id, GeneroRequestDTO dto) {
        Genero entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gênero não encontrado"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private GeneroResponseDTO toResponseDTO(Genero entity) {
        GeneroResponseDTO dto = new GeneroResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}