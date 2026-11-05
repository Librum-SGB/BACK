package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.mapper.MaterialMapper;
import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import com.sgb.mylibrum.specifications.MaterialFiltro;
import com.sgb.mylibrum.specifications.MaterialSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Transactional
    public MaterialResponseDTO create(MaterialRequestDTO materialRequestDTO) {

        Material material = materialMapper.toEntity(materialRequestDTO);

        Material materialSalvo = materialRepository.save(material);
        log.info("Material criado: id={}", materialSalvo.getId());

        return materialMapper.toResponseDTO(materialSalvo);
    }

    @Transactional
    public MaterialResponseDTO update(Long id, MaterialRequestDTO materialRequestDTO) {

        Material materialExistente = materialRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Material nao encontrado para atualizacao: id={}", id);
                    return new RuntimeException("Material n?o encontrado");
                });

        materialMapper.updateEntity(materialRequestDTO, materialExistente);
        Material materialSalvo = materialRepository.save(materialExistente);
        log.info("Material atualizado: id={}", materialSalvo.getId());

        return materialMapper.toResponseDTO(materialSalvo);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findAll(MaterialFiltro filtro) {
        log.debug("Buscando materiais com filtros");

        return materialRepository.findAll(MaterialSpecification.filtrar(filtro)).stream()
                .map(materialMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO findById(Long id) {
        log.debug("Buscando material: id={}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Material nao encontrado: id={}", id);
                    return new RuntimeException("Material n?o encontrado");
                });

        return materialMapper.toResponseDTO(material);
    }

    @Transactional
    public void delete(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Material nao encontrado para exclusao: id={}", id);
                    return new RuntimeException("Material n?o encontrado");
                });
        material.setExcluido(true);
        materialRepository.save(material);
        log.info("Material excluido: id={}", material.getId());
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO findByIsbn(String isbn) {
        log.debug("Buscando material por ISBN");
        Material material = materialRepository.findByIsbn(isbn)
                .orElseThrow(() -> {
                    log.warn("Material nao encontrado por ISBN");
                    return new RuntimeException("Material n?o encontrado");
                });

        return materialMapper.toResponseDTO(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findByTitulo(String titulo, Boolean ativo) {
        log.debug("Buscando materiais por titulo: ativo={}", ativo);

        return materialRepository.findAll(AtivoSpecification.<Material>ativo(ativo)
                        .and(AtivoSpecification.contemIgnoreCase("titulo", titulo)))
                .stream()
                .map(materialMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findByEditoraId(Long editoraId, Boolean ativo) {
        log.debug("Buscando materiais por editora: editoraId={}, ativo={}", editoraId, ativo);

        return materialRepository.findAll(AtivoSpecification.<Material>ativo(ativo)
                        .and(AtivoSpecification.igual("editora.id", editoraId)))
                .stream()
                .map(materialMapper::toResponseDTO)
                .toList();
    }
}