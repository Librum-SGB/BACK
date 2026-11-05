package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
import com.sgb.mylibrum.entities.Filial;

@Mapper(config = MapeamentoConfig.class)
public interface FilialMapper {

    default Filial toEntity(FilialRequestDTO dto) {
        Filial filial = new Filial();
        updateEntity(dto, filial);
        return filial;
    }

    // "ativo" vem do DTO, por isso não usa IgnorarCamposDeEntidade.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataUltimaAtualizacao", ignore = true)
    @Mapping(target = "excluido", ignore = true)
    @Mapping(target = "estantes", ignore = true)
    @Mapping(target = "exemplares", ignore = true)
    void updateEntity(FilialRequestDTO dto, @MappingTarget Filial filial);

    FilialResponseDTO toResponseDTO(Filial filial);
}
