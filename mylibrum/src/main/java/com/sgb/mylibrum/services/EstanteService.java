package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.mapper.EstanteMapper;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.exceptions.BusinessException;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EstanteRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class EstanteService {

    private final EstanteRepository repository;
    private final FilialRepository filialRepository;
    private final ExemplarRepository exemplarRepository;
    private final EstanteMapper mapper;

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findAll(Boolean ativo) {
        log.debug("Buscando estantes: ativo={}", ativo);
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findByFilialId(Long filialId, Boolean ativo) {
        log.debug("Buscando estantes da filial: filialId={}, ativo={}", filialId, ativo);
        if (!filialRepository.existsById(filialId)) {
            log.warn("Filial nao encontrada ao buscar estantes: filialId={}", filialId);
            throw new ResourceNotFoundException("Filial n?o encontrada");
        }
        return repository.findAll(AtivoSpecification.<Estante>ativo(ativo)
                        .and(AtivoSpecification.igual("filial.id", filialId))).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstanteResponseDTO findById(Long id) {
        log.debug("Buscando estante: id={}", id);
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estante nao encontrada: id={}", id);
                    return new RuntimeException("Estante n?o encontrada");
                }));
    }

    @Transactional
    public EstanteResponseDTO create(EstanteRequestDTO dto) {
        Estante entity = mapper.toEntity(dto);
        setRelacionamentos(dto, entity);
        Estante salva = repository.save(entity);
        log.info("Estante criada: id={}", salva.getId());
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public EstanteResponseDTO update(Long id, EstanteRequestDTO dto) {
        Estante entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estante nao encontrada para atualizacao: id={}", id);
                    return new RuntimeException("Estante n?o encontrada");
                });
        mapper.updateEntity(dto, entity);
        setRelacionamentos(dto, entity);
        Estante atualizada = repository.save(entity);
        log.info("Estante atualizada: id={}", atualizada.getId());
        return mapper.toResponseDTO(atualizada);
    }

    @Transactional
    public void delete(Long id) {
        if (exemplarRepository.existsByEstanteId(id)) {
            log.warn("Exclusao de estante rejeitada por conter exemplares: id={}", id);
            throw new BusinessException("N?o ? poss?vel excluir a estante porque ela possui exemplares");
        }
        Estante estante = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Estante nao encontrada para exclusao: id={}", id);
                    return new RuntimeException("Estante n?o encontrada");
                });
        estante.setExcluido(true);
        repository.save(estante);
        log.info("Estante excluida: id={}", estante.getId());
    }

    private void setRelacionamentos(EstanteRequestDTO dto, Estante entity) {
        entity.setFilial(filialRepository.findById(dto.getFilialId())
                .orElseThrow(() -> {
                    log.warn("Filial nao encontrada para estante: filialId={}", dto.getFilialId());
                    return new RuntimeException("Filial n?o encontrada");
                }));
    }
}