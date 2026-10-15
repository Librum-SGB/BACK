package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.mapper.FilialMapper;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FilialService {

    private final FilialRepository repository;
    private final FilialMapper mapper;

    @Transactional(readOnly = true)
    public List<FilialResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public FilialResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filial não encontrada")));
    }

    @Transactional
    public FilialResponseDTO create(FilialRequestDTO dto) {
        return mapper.toResponseDTO(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public FilialResponseDTO update(Long id, FilialRequestDTO dto) {
        Filial entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        mapper.updateEntity(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Filial filial = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        filial.setExcluido(true);
        repository.save(filial);
    }
}