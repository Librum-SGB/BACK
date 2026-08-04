package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.EmprestimoDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository repository;

    @Transactional(readOnly = true)
    public List<EmprestimoDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmprestimoDTO findById(Long id) {
        Emprestimo entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public EmprestimoDTO create(EmprestimoDTO dto) {
        Emprestimo entity = new Emprestimo();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public EmprestimoDTO update(Long id, EmprestimoDTO dto) {
        Emprestimo entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private EmprestimoDTO toDTO(Emprestimo entity) {
        EmprestimoDTO dto = new EmprestimoDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getUsuario() != null) dto.setUsuarioId(entity.getUsuario().getId());
        if (entity.getExemplar() != null) dto.setExemplarId(entity.getExemplar().getId());
        if (entity.getGestor() != null) dto.setGestorId(entity.getGestor().getId());
        return dto;
    }
}