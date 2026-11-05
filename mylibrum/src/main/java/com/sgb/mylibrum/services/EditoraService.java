package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EditoraRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import com.sgb.mylibrum.dtos.mapper.EditoraMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EditoraService {

    private final EditoraRepository repository;
    private final EditoraMapper mapper;

    @Transactional(readOnly = true)
    public List<EditoraResponseDTO> findAll(Boolean ativo) {
        log.debug("Buscando editoras: ativo={}", ativo);
        return repository.findAll(AtivoSpecification.ativo(ativo))
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EditoraResponseDTO findById(Long id) {
        log.debug("Buscando editora: id={}", id);
        return mapper.toResponseDTO(findEntityById(id));
    }

    @Transactional
    public EditoraResponseDTO create(EditoraRequestDTO dto) {
        Editora entity = mapper.toEntity(dto);
        Editora salva = repository.save(entity);
        log.info("Editora criada: id={}", salva.getId());
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public EditoraResponseDTO update(Long id, EditoraRequestDTO dto) {
        Editora entity = findEntityById(id);
        mapper.updateEntity(dto, entity);
        Editora atualizada = repository.save(entity);
        log.info("Editora atualizada: id={}", atualizada.getId());
        return mapper.toResponseDTO(atualizada);
    }

    @Transactional
    public void delete(Long id) {
        Editora entity = findEntityById(id);
        entity.setExcluido(true);
        repository.save(entity);
        log.info("Editora excluida: id={}", entity.getId());
    }

    private Editora findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Editora nao encontrada: id={}", id);
                    return new ResourceNotFoundException("Editora n?o encontrada com id: " + id);
                });
    }
}
