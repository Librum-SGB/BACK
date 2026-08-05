package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.GestorRequestDTO;
import com.sgb.mylibrum.dtos.response.GestorResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Gestor;
import com.sgb.mylibrum.repositories.GestorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestorService {

    private final GestorRepository repository;

    @Transactional(readOnly = true)
    public List<GestorResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GestorResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gestor não encontrado")));
    }

    @Transactional
    public GestorResponseDTO create(GestorRequestDTO dto) {
        Gestor entity = new Gestor();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            entity.setFilial(filial);
        }
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public GestorResponseDTO update(Long id, GestorRequestDTO dto) {
        Gestor entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            entity.setFilial(filial);
        }
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private GestorResponseDTO toResponseDTO(Gestor entity) {
        GestorResponseDTO dto = new GestorResponseDTO();
        BeanUtils.copyProperties(entity, dto, "senha"); // Omitindo a senha na resposta
        if (entity.getFilial() != null) dto.setFilialId(entity.getFilial().getId());
        return dto;
    }
}