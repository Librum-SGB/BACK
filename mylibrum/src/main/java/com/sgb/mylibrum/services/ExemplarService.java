package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.ExemplarDTO;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository repository;

    @Transactional(readOnly = true)
    public List<ExemplarDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExemplarDTO findById(Long id) {
        Exemplar entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exemplar não encontrado com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public ExemplarDTO create(ExemplarDTO dto) {
        Exemplar entity = new Exemplar();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public ExemplarDTO update(Long id, ExemplarDTO dto) {
        Exemplar entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exemplar não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ExemplarDTO toDTO(Exemplar entity) {
        ExemplarDTO dto = new ExemplarDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getLivro() != null) dto.setLivroId(entity.getLivro().getId());
        if (entity.getFilial() != null) dto.setFilialId(entity.getFilial().getId());
        if (entity.getEstante() != null) dto.setEstanteId(entity.getEstante().getId());
        return dto;
    }
}