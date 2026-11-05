package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sgb.mylibrum.dtos.request.AutorRequestDTO;
import com.sgb.mylibrum.dtos.response.AutorResponseDTO;
import com.sgb.mylibrum.entities.Autor;

@Mapper(config = MapeamentoConfig.class)
public interface AutorMapper {

    default Autor toEntity(AutorRequestDTO dto) {
        Autor autor = new Autor();
        updateEntity(dto, autor);
        return autor;
    }

    @IgnorarCamposDeEntidade
    void updateEntity(AutorRequestDTO dto, @MappingTarget Autor autor);

    AutorResponseDTO toResponseDTO(Autor autor);
}
