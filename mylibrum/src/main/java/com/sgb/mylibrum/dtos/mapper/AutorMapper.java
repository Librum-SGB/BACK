package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.AutorRequestDTO;
import com.sgb.mylibrum.dtos.response.AutorResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AutorMapper {

    public Autor toEntity(AutorRequestDTO dto) {
        Autor autor = new Autor();
        updateEntity(dto, autor);
        return autor;
    }

    public void updateEntity(AutorRequestDTO dto, Autor autor) {
        BeanUtils.copyProperties(dto, autor, "id", "dataCriacao", "dataUltimaAtualizacao");
    }

    public AutorResponseDTO toResponseDTO(Autor autor) {
        AutorResponseDTO dto = new AutorResponseDTO();
        BeanUtils.copyProperties(autor, dto);
        return dto;
    }
}
