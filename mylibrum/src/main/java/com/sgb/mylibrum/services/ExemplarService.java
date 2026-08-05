package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Livro;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExemplarService {

    private final ExemplarRepository repository;

    @Transactional(readOnly = true)
    public List<ExemplarResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExemplarResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exemplar não encontrado")));
    }

    @Transactional
    public ExemplarResponseDTO create(ExemplarRequestDTO dto) {
        Exemplar entity = new Exemplar();
        BeanUtils.copyProperties(dto, entity);
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public ExemplarResponseDTO update(Long id, ExemplarRequestDTO dto) {
        Exemplar entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exemplar não encontrado"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void setRelacionamentos(ExemplarRequestDTO dto, Exemplar entity) {
        if (dto.getLivroId() != null) {
            Livro livro = new Livro();
            livro.setId(dto.getLivroId());
            entity.setLivro(livro);
        }
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            entity.setFilial(filial);
        }
        if (dto.getEstanteId() != null) {
            Estante estante = new Estante();
            estante.setId(dto.getEstanteId());
            entity.setEstante(estante);
        }
    }

    private ExemplarResponseDTO toResponseDTO(Exemplar entity) {
        ExemplarResponseDTO dto = new ExemplarResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getLivro() != null) dto.setLivroId(entity.getLivro().getId());
        if (entity.getFilial() != null) dto.setFilialId(entity.getFilial().getId());
        if (entity.getEstante() != null) dto.setEstanteId(entity.getEstante().getId());
        return dto;
    }
}