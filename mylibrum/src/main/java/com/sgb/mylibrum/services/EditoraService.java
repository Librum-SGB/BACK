package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.EditoraDTO;
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
    public List<EditoraDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EditoraDTO findById(Long id) {
        Editora entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editora não encontrada com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public EditoraDTO create(EditoraDTO dto) {
        Editora entity = new Editora();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public EditoraDTO update(Long id, EditoraDTO dto) {
        Editora entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editora não encontrada com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private EditoraDTO toDTO(Editora entity) {
        EditoraDTO dto = new EditoraDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}