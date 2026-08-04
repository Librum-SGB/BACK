package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.FilialDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.repositories.FilialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilialService {

    private final FilialRepository repository;

    @Transactional(readOnly = true)
    public List<FilialDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FilialDTO findById(Long id) {
        Filial entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filial não encontrada com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public FilialDTO create(FilialDTO dto) {
        Filial entity = new Filial();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public FilialDTO update(Long id, FilialDTO dto) {
        Filial entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filial não encontrada com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private FilialDTO toDTO(Filial entity) {
        FilialDTO dto = new FilialDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}