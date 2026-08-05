package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.repositories.EditoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository repository;

    @Transactional(readOnly = true)
    public List<EditoraResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EditoraResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editora não encontrada")));
    }

    @Transactional
    public EditoraResponseDTO create(EditoraRequestDTO dto) {
        Editora entity = new Editora();
        BeanUtils.copyProperties(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public EditoraResponseDTO update(Long id, EditoraRequestDTO dto) {
        Editora entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editora não encontrada"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private EditoraResponseDTO toResponseDTO(Editora entity) {
        EditoraResponseDTO dto = new EditoraResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}