package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.mapper.ExemplarMapper;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository repository;
    private final MaterialRepository materialRepository;
    private final ExemplarMapper mapper;

    @Transactional(readOnly = true)
    public List<ExemplarResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public ExemplarResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exemplar não encontrado")));
    }

    @Transactional
    public ExemplarResponseDTO create(ExemplarRequestDTO dto) {
        Exemplar entity = mapper.toEntity(dto);
        Exemplar salvo = repository.save(entity);
        atualizarQuantidadeExemplares(salvo.getMaterial());
        return mapper.toResponseDTO(salvo);
    }

    @Transactional
    public ExemplarResponseDTO update(Long id, ExemplarRequestDTO dto) {
        Exemplar entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exemplar não encontrado"));
        mapper.updateEntity(dto, entity);
        Exemplar salvo = repository.save(entity);
        atualizarQuantidadeExemplares(salvo.getMaterial());
        return mapper.toResponseDTO(salvo);
    }

    @Transactional
    public void delete(Long id) {
        Exemplar exemplar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exemplar não encontrado"));
        exemplar.setExcluido(true);
        repository.save(exemplar);
        atualizarQuantidadeExemplares(exemplar.getMaterial());
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