package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;

@Mapper(config = MapeamentoConfig.class)
public interface EstanteMapper {

    default Estante toEntity(EstanteRequestDTO dto) {
        Estante estante = new Estante();
        updateEntity(dto, estante);
        return estante;
    }

    @IgnorarCamposDeEntidade
    @Mapping(target = "exemplares", ignore = true)
    @Mapping(target = "filial", ignore = true)
    @Mapping(target = "prateleiras", ignore = true)
    void updateEntity(EstanteRequestDTO dto, @MappingTarget Estante estante);

    @Mapping(target = "filialId", source = "filial.id")
    EstanteResponseDTO toResponseDTO(Estante estante);
}