package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.dtos.response.GeneroResponseDTO;
import com.sgb.mylibrum.entities.Genero;

@Mapper(config = MapeamentoConfig.class)
public interface GeneroMapper {

    default Genero toEntity(GeneroRequestDTO dto) {
        Genero genero = new Genero();
        updateEntity(dto, genero);
        return genero;
    }

    @IgnorarCamposDeEntidade
    void updateEntity(GeneroRequestDTO dto, @MappingTarget Genero genero);

    GeneroResponseDTO toResponseDTO(Genero genero);
}
