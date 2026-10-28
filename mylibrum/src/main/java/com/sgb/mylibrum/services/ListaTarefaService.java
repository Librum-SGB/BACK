package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.mapper.ListaTarefaMapper;
import com.sgb.mylibrum.dtos.response.ListaTarefaResponseDTO;
import com.sgb.mylibrum.entities.ListaTarefa;
import com.sgb.mylibrum.repositories.ListaTarefaRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ListaTarefaService {

    private final ListaTarefaRepository repository;
    private final ListaTarefaMapper mapper;

    @Transactional(readOnly = true)
    public List<ListaTarefaResponseDTO> findAll(Boolean ativo) {
        log.debug("Buscando tarefas: ativo={}", ativo);
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ListaTarefaResponseDTO> findByGestorId(Long gestorId) {
        log.debug("Buscando tarefas do gestor: gestorId={}", gestorId);
        return repository.findByUsuarioId(gestorId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ListaTarefaResponseDTO findById(Long id) {
        log.debug("Buscando tarefa: id={}", id);
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tarefa nao encontrada: id={}", id);
                    return new RuntimeException("Tarefa n?o encontrada");
                }));
    }

    @Transactional
    public ListaTarefaResponseDTO create(ListaTarefaRequestDTO dto) {
        ListaTarefa tarefa = repository.save(mapper.toEntity(dto));
        log.info("Tarefa criada: id={}", tarefa.getId());
        return mapper.toResponseDTO(tarefa);
    }

    @Transactional
    public ListaTarefaResponseDTO update(Long id, ListaTarefaRequestDTO dto) {
        ListaTarefa entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tarefa nao encontrada para atualizacao: id={}", id);
                    return new RuntimeException("Tarefa n?o encontrada");
                });
        mapper.updateEntity(dto, entity);
        ListaTarefa atualizada = repository.save(entity);
        log.info("Tarefa atualizada: id={}", atualizada.getId());
        return mapper.toResponseDTO(atualizada);
    }

    @Transactional
    public void delete(Long id) {
        ListaTarefa tarefa = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tarefa nao encontrada para exclusao: id={}", id);
                    return new RuntimeException("Tarefa n?o encontrada");
                });
        tarefa.setExcluido(true);
        repository.save(tarefa);
        log.info("Tarefa excluida: id={}", tarefa.getId());
    }
}