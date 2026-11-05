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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository repository;
    private final AutorMapper mapper;

    @Transactional(readOnly = true)
    public List<AutorResponseDTO> findAll(Boolean ativo) {
        log.debug("Buscando autores: ativo={}", ativo);
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public AutorResponseDTO findById(Long id) {
        log.debug("Buscando autor: id={}", id);
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Autor nao encontrado: id={}", id);
                    return new ResourceNotFoundException("Autor n?o encontrado com id: " + id);
                }));
    }

    @Transactional
    public AutorResponseDTO create(AutorRequestDTO dto) {
        Autor autor = repository.save(mapper.toEntity(dto));
        log.info("Autor criado: id={}", autor.getId());
        return mapper.toResponseDTO(autor);
    }

    @Transactional
    public AutorResponseDTO update(Long id, AutorRequestDTO dto) {
        Autor entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Autor nao encontrado para atualizacao: id={}", id);
                    return new ResourceNotFoundException("Autor n?o encontrado com id: " + id);
                });
        mapper.updateEntity(dto, entity);
        Autor atualizado = repository.save(entity);
        log.info("Autor atualizado: id={}", atualizado.getId());
        return mapper.toResponseDTO(atualizado);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Autor nao encontrado para exclusao: id={}", id);
            throw new ResourceNotFoundException("Autor n?o encontrado com id: " + id);
        }
        Autor autor = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Autor nao encontrado para exclusao: id={}", id);
                    return new ResourceNotFoundException("Autor n?o encontrado com id: " + id);
                });
        autor.setExcluido(true);
        repository.save(autor);
        log.info("Autor excluido: id={}", autor.getId());
    }
}