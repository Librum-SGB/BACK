package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
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
    public List<FilialResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FilialResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filial não encontrada")));
    }

    @Transactional
    public FilialResponseDTO create(FilialRequestDTO dto) {
        Filial entity = new Filial();
        BeanUtils.copyProperties(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public FilialResponseDTO update(Long id, FilialRequestDTO dto) {
        Filial entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private FilialResponseDTO toResponseDTO(Filial entity) {
        FilialResponseDTO dto = new FilialResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}