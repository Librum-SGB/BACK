package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.LivroDTO;
import com.sgb.mylibrum.entities.Autor;
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
    public List<LivroDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LivroDTO findById(Long id) {
        Livro entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public LivroDTO create(LivroDTO dto) {
        Livro entity = new Livro();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao", "autores", "generos");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public LivroDTO update(Long id, LivroDTO dto) {
        Livro entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao", "autores", "generos");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private LivroDTO toDTO(Livro entity) {
        LivroDTO dto = new LivroDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getEditora() != null) dto.setEditoraId(entity.getEditora().getId());
        
        if (entity.getAutores() != null) {
            dto.setAutorIds(entity.getAutores().stream().map(Autor::getId).collect(Collectors.toSet()));
        }
        if (entity.getGeneros() != null) {
            dto.setGeneroIds(entity.getGeneros().stream().map(Genero::getId).collect(Collectors.toSet()));
        }
        return dto;
    }
}