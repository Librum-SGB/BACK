package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EditoraRepository;
import com.sgb.mylibrum.utils.EditoraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository repository;
    private final EditoraMapper mapper;

    @Transactional(readOnly = true)
    public List<EditoraResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EditoraResponseDTO findById(Long id) {
        return mapper.toResponseDTO(findEntityById(id));
    }

    @Transactional
    public EditoraResponseDTO create(EditoraRequestDTO dto) {
        Editora entity = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public EditoraResponseDTO update(Long id, EditoraRequestDTO dto) {
        Editora entity = findEntityById(id);
        mapper.updateEntity(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Editora entity = findEntityById(id);
        repository.delete(entity);
    }

    private Editora findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editora não encontrada com id: " + id));
    }
}
