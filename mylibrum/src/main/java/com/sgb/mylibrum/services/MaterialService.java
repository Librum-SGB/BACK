package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository repository;

    @Transactional(readOnly = true)
    public List<MaterialResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterialResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado")));
    }

    @Transactional
    public MaterialResponseDTO create(MaterialRequestDTO dto) {
        Material entity = new Material();
        BeanUtils.copyProperties(dto, entity, "autorIds", "generoIds");
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public MaterialResponseDTO update(Long id, MaterialRequestDTO dto) {
        Material entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao", "autorIds", "generoIds");
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void setRelacionamentos(MaterialRequestDTO dto, Material entity) {
        if (dto.getEditoraId() != null) {
            Editora editora = new Editora();
            editora.setId(dto.getEditoraId());
            entity.setEditora(editora);
        }
        if (dto.getAutorId() != null) {
            Autor autor = new Autor();
            autor.setId(dto.getAutorId());
            entity.setAutor(autor);
        }
        if (dto.getAutorIds() != null) {
            entity.setAutores(dto.getAutorIds().stream().map(id -> {
                Autor autor = new Autor();
                autor.setId(id);
                return autor;
            }).collect(Collectors.toSet()));
        }
        if (dto.getGeneroIds() != null) {
            entity.setGeneros(dto.getGeneroIds().stream().map(id -> {
                Genero genero = new Genero();
                genero.setId(id);
                return genero;
            }).collect(Collectors.toSet()));
        }
    }

    private MaterialResponseDTO toResponseDTO(Material entity) {
        MaterialResponseDTO dto = new MaterialResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getEditora() != null) {
            dto.setEditoraId(entity.getEditora().getId());
        }
        if (entity.getAutor() != null) {
            dto.setAutorId(entity.getAutor().getId());
        }
        if (entity.getAutores() != null) {
            dto.setAutorIds(entity.getAutores().stream().map(Autor::getId).collect(Collectors.toSet()));
        }
        if (entity.getGeneros() != null) {
            dto.setGeneroIds(entity.getGeneros().stream().map(Genero::getId).collect(Collectors.toSet()));
        }
        return dto;
    }
}
