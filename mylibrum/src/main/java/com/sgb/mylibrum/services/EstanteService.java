package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.repositories.EstanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstanteService {

    private final EstanteRepository repository;

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EstanteResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada")));
    }

    @Transactional
    public EstanteResponseDTO create(EstanteRequestDTO dto) {
        Estante entity = new Estante();
        BeanUtils.copyProperties(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public EstanteResponseDTO update(Long id, EstanteRequestDTO dto) {
        Estante entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private EstanteResponseDTO toResponseDTO(Estante entity) {
        EstanteResponseDTO dto = new EstanteResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}