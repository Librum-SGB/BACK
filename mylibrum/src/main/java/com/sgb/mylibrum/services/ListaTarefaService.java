package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.mapper.ListaTarefaMapper;
import com.sgb.mylibrum.dtos.response.ListaTarefaResponseDTO;
import com.sgb.mylibrum.entities.ListaTarefa;
import com.sgb.mylibrum.repositories.ListaTarefaRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ListaTarefaService {

    private final ListaTarefaRepository repository;
    private final ListaTarefaMapper mapper;

    @Transactional(readOnly = true)
    public List<ListaTarefaResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ListaTarefaResponseDTO> findByGestorId(Long gestorId) {
        return repository.findByUsuarioId(gestorId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ListaTarefaResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada")));
    }

    @Transactional
    public ListaTarefaResponseDTO create(ListaTarefaRequestDTO dto) {
        return mapper.toResponseDTO(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public ListaTarefaResponseDTO update(Long id, ListaTarefaRequestDTO dto) {
        ListaTarefa entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        mapper.updateEntity(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        ListaTarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        tarefa.setExcluido(true);
        repository.save(tarefa);
    }
}