package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.mapper.ExemplarMapper;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.specifications.ExemplarFiltro;
import com.sgb.mylibrum.specifications.ExemplarSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository repository;
    private final MaterialRepository materialRepository;
    private final ExemplarMapper mapper;

    @Transactional(readOnly = true)
    public List<ExemplarResponseDTO> findAll(ExemplarFiltro filtro) {
        log.debug("Buscando exemplares com filtros");
        return repository.findAll(ExemplarSpecification.filtrar(filtro)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public ExemplarResponseDTO findById(Long id) {
        log.debug("Buscando exemplar: id={}", id);
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Exemplar nao encontrado: id={}", id);
                    return new RuntimeException("Exemplar n?o encontrado");
                }));
    }

    @Transactional
    public ExemplarResponseDTO create(ExemplarRequestDTO dto) {
        Exemplar entity = mapper.toEntity(dto);
        Exemplar salvo = repository.save(entity);
        atualizarQuantidadeExemplares(salvo.getMaterial());
        log.info("Exemplar criado: id={}", salvo.getId());
        return mapper.toResponseDTO(salvo);
    }

    @Transactional
    public ExemplarResponseDTO update(Long id, ExemplarRequestDTO dto) {
        Exemplar entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Exemplar nao encontrado para atualizacao: id={}", id);
                    return new RuntimeException("Exemplar n?o encontrado");
                });
        mapper.updateEntity(dto, entity);
        Exemplar salvo = repository.save(entity);
        atualizarQuantidadeExemplares(salvo.getMaterial());
        log.info("Exemplar atualizado: id={}", salvo.getId());
        return mapper.toResponseDTO(salvo);
    }

    @Transactional
    public void delete(Long id) {
        Exemplar exemplar = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Exemplar nao encontrado para exclusao: id={}", id);
                    return new RuntimeException("Exemplar n?o encontrado");
                });
        exemplar.setExcluido(true);
        repository.save(exemplar);
        atualizarQuantidadeExemplares(exemplar.getMaterial());
        log.info("Exemplar excluido: id={}", exemplar.getId());
    }

    private void atualizarQuantidadeExemplares(Material material) {
        if (material == null || material.getId() == null) {
            return;
        }

        Material materialPersistido = materialRepository.findById(material.getId())
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        long quantidade = repository.findByMaterialId(materialPersistido.getId()).size();
        materialPersistido.setQuantidadeExemplares((int) quantidade);
        materialRepository.save(materialPersistido);
    }

}