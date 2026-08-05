package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.response.ListaTarefaResponseDTO;
import com.sgb.mylibrum.entities.Gestor;
import com.sgb.mylibrum.entities.ListaTarefa;
import com.sgb.mylibrum.repositories.ListaTarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListaTarefaService {

    private final ListaTarefaRepository repository;

    @Transactional(readOnly = true)
    public List<ListaTarefaResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ListaTarefaResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada")));
    }

    @Transactional
    public ListaTarefaResponseDTO create(ListaTarefaRequestDTO dto) {
        ListaTarefa entity = new ListaTarefa();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getGestorId() != null) {
            Gestor gestor = new Gestor();
            gestor.setId(dto.getGestorId());
            entity.setGestor(gestor);
        }
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public ListaTarefaResponseDTO update(Long id, ListaTarefaRequestDTO dto) {
        ListaTarefa entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        if (dto.getGestorId() != null) {
            Gestor gestor = new Gestor();
            gestor.setId(dto.getGestorId());
            entity.setGestor(gestor);
        }
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ListaTarefaResponseDTO toResponseDTO(ListaTarefa entity) {
        ListaTarefaResponseDTO dto = new ListaTarefaResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getGestor() != null) dto.setGestorId(entity.getGestor().getId());
        return dto;
    }
}