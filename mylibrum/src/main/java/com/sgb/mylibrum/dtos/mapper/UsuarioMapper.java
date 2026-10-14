package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Usuario;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        updateEntity(dto, usuario);
        return usuario;
    }

    public void updateEntity(UsuarioRequestDTO dto, Usuario usuario) {
        BeanUtils.copyProperties(dto, usuario,
                "id", "dataCriacao", "dataUltimaAtualizacao", "senha", "filialId", "funcao");
        if (dto.getFuncao() != null) {
            usuario.setFuncao(dto.getFuncao());
        }
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            usuario.setFilial(filial);
        }
    }

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        BeanUtils.copyProperties(usuario, dto);
        if (usuario.getFilial() != null) {
            dto.setFilialId(usuario.getFilial().getId());
        }
        return dto;
    }
}
