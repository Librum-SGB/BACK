package com.sgb.mylibrum.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.Funcao;
import com.sgb.mylibrum.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

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
        BeanUtils.copyProperties(dto, entity, "senha");
        if (dto.getFuncao() == null) {
            entity.setFuncao(Funcao.USUARIO);
        }
        entity.setSenha(passwordEncoder.encode(dto.getSenha()));
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
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao", "senha", "funcao");
        if (dto.getFuncao() != null) {
            entity.setFuncao(dto.getFuncao());
        }
        entity.setSenha(passwordEncoder.encode(dto.getSenha()));
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