package com.sgb.mylibrum.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgb.mylibrum.dtos.mapper.AutorMapper;
import com.sgb.mylibrum.dtos.request.AutorRequestDTO;
import com.sgb.mylibrum.dtos.response.AutorResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.AutorRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository repository;
    private final AutorMapper mapper;

    @Transactional(readOnly = true)
    public List<AutorResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public AutorResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com id: " + id)));
    }

    @Transactional
    public AutorResponseDTO create(AutorRequestDTO dto) {
        return mapper.toResponseDTO(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public AutorResponseDTO update(Long id, AutorRequestDTO dto) {
        Autor entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com id: " + id));
        mapper.updateEntity(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Autor não encontrado com id: " + id);
        }
        Autor autor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado com id: " + id));
        autor.setExcluido(true);
        repository.save(autor);
    }
}