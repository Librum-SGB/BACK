package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado")));
    }

    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        Usuario entity = new Usuario();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            entity.setFilial(filial);
        }
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
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

    private UsuarioResponseDTO toResponseDTO(Usuario entity) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getFilial() != null) dto.setFilialId(entity.getFilial().getId());
        return dto;
    }
}