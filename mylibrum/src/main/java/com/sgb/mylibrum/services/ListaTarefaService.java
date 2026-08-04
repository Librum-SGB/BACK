package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.ListaTarefaDTO;
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
    public List<ListaTarefaDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ListaTarefaDTO findById(Long id) {
        ListaTarefa entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public ListaTarefaDTO create(ListaTarefaDTO dto) {
        ListaTarefa entity = new ListaTarefa();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public ListaTarefaDTO update(Long id, ListaTarefaDTO dto) {
        ListaTarefa entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ListaTarefaDTO toDTO(ListaTarefa entity) {
        ListaTarefaDTO dto = new ListaTarefaDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getGestor() != null) dto.setGestorId(entity.getGestor().getId());
        return dto;
    }
}