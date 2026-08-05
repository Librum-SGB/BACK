package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.LivroRequestDTO;
import com.sgb.mylibrum.dtos.response.LivroResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.entities.Livro;
import com.sgb.mylibrum.repositories.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository repository;

    @Transactional(readOnly = true)
    public List<LivroResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LivroResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado")));
    }

    @Transactional
    public LivroResponseDTO create(LivroRequestDTO dto) {
        Livro entity = new Livro();
        BeanUtils.copyProperties(dto, entity, "autorIds", "generoIds");
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public LivroResponseDTO update(Long id, LivroRequestDTO dto) {
        Livro entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao", "autorIds", "generoIds");
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void setRelacionamentos(LivroRequestDTO dto, Livro entity) {
        if (dto.getEditoraId() != null) {
            Editora editora = new Editora();
            editora.setId(dto.getEditoraId());
            entity.setEditora(editora);
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

    private LivroResponseDTO toResponseDTO(Livro entity) {
        LivroResponseDTO dto = new LivroResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getEditora() != null) {
            dto.setEditoraId(entity.getEditora().getId());
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