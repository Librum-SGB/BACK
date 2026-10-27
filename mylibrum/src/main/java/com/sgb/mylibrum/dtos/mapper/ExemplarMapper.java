package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.entities.Exemplar;

@Mapper(config = MapeamentoConfig.class)
public interface ExemplarMapper {

    default Exemplar toEntity(ExemplarRequestDTO dto) {
        Exemplar exemplar = new Exemplar();
        updateEntity(dto, exemplar);
        return exemplar;
    }

    @IgnorarCamposDeEntidade
    @Mapping(target = "material", source = "materialId",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "filial", source = "filialId",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "estante", source = "estanteId",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ExemplarRequestDTO dto, @MappingTarget Exemplar exemplar);

    @Mapping(target = "materialId", source = "material.id")
    @Mapping(target = "filialId", source = "filial.id")
    @Mapping(target = "estanteId", source = "estante.id")
    ExemplarResponseDTO toResponseDTO(Exemplar exemplar);
}
