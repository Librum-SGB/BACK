package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.mapper.MaterialMapper;
import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Transactional
    public MaterialResponseDTO create(MaterialRequestDTO materialRequestDTO) {

        Material material = materialMapper.toEntity(materialRequestDTO);

        Material materialSalvo = materialRepository.save(material);

        return materialMapper.toResponseDTO(materialSalvo);
    }

    @Transactional
    public MaterialResponseDTO update(Long id, MaterialRequestDTO materialRequestDTO) {

        Material materialExistente = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        materialMapper.updateEntity(materialRequestDTO, materialExistente);
        Material materialSalvo = materialRepository.save(materialExistente);

        return materialMapper.toResponseDTO(materialSalvo);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findAll(Boolean ativo) {

        return materialRepository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(materialMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO findById(Long id) {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        return materialMapper.toResponseDTO(material);
    }

    @Transactional
    public void delete(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
        material.setExcluido(true);
        materialRepository.save(material);
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO findByIsbn(String isbn) {

        Material material = materialRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        return materialMapper.toResponseDTO(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findByTitulo(String titulo, Boolean ativo) {

        return materialRepository.findAll(AtivoSpecification.<Material>ativo(ativo)
                        .and(AtivoSpecification.contemIgnoreCase("titulo", titulo)))
                .stream()
                .map(materialMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findByEditoraId(Long editoraId, Boolean ativo) {

        return materialRepository.findAll(AtivoSpecification.<Material>ativo(ativo)
                        .and(AtivoSpecification.igual("editora.id", editoraId)))
                .stream()
                .map(materialMapper::toResponseDTO)
                .toList();
    }
}