package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.mapper.MaterialMapper;
import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.MaterialRepository;
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

        return materialMapper.toDto(materialSalvo);
    }

    @Transactional
    public MaterialResponseDTO update(Long id, MaterialRequestDTO materialRequestDTO) {

        Material materialExistente = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        Material materialAtualizado = materialMapper.toEntity(materialRequestDTO);

        materialAtualizado.setId(materialExistente.getId());

        Material materialSalvo = materialRepository.save(materialAtualizado);

        return materialMapper.toDto(materialSalvo);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findAll() {

        return materialRepository.findAll().stream()
                .map(materialMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO findById(Long id) {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        return materialMapper.toDto(material);
    }

    @Transactional
    public void delete(Long id) {

        materialRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO findByIsbn(String isbn) {

        Material material = materialRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));

        return materialMapper.toDto(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findByTitulo(String titulo) {

        return materialRepository.findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(materialMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findByEditoraId(Long editoraId) {

        return materialRepository.findByEditoraId(editoraId)
                .stream()
                .map(materialMapper::toDto)
                .toList();
    }
}