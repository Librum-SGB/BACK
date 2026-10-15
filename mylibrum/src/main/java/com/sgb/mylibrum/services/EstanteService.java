package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.mapper.EstanteMapper;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EstanteRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EstanteService {

    private final EstanteRepository repository;
    private final FilialRepository filialRepository;
    private final ExemplarRepository exemplarRepository;
    private final EstanteMapper mapper;

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findByFilialId(Long filialId, Boolean ativo) {
        if (!filialRepository.existsById(filialId)) {
            throw new ResourceNotFoundException("Filial não encontrada");
        }
        return repository.findAll(AtivoSpecification.<Estante>ativo(ativo)
                        .and(AtivoSpecification.igual("filial.id", filialId))).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstanteResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada")));
    }

    @Transactional
    public EstanteResponseDTO create(EstanteRequestDTO dto) {
        Estante entity = mapper.toEntity(dto);
        setRelacionamentos(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public EstanteResponseDTO update(Long id, EstanteRequestDTO dto) {
        Estante entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada"));
        mapper.updateEntity(dto, entity);
        setRelacionamentos(dto, entity);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (exemplarRepository.existsByEstanteId(id)) {
            throw new IllegalStateException("Não é possível excluir a estante porque ela possui exemplares");
        }
        Estante estante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada"));
        estante.setExcluido(true);
        repository.save(estante);
    }

    private void setRelacionamentos(EstanteRequestDTO dto, Estante entity) {
        entity.setFilial(filialRepository.findById(dto.getFilialId())
                .orElseThrow(() -> new RuntimeException("Filial não encontrada")));
    }
}