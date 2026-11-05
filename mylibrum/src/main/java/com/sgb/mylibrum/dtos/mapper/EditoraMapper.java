package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.entities.Editora;

@Mapper(config = MapeamentoConfig.class)
public interface EditoraMapper {

    default Editora toEntity(EditoraRequestDTO dto) {
        Editora editora = new Editora();
        updateEntity(dto, editora);
        return editora;
    }

    @IgnorarCamposDeEntidade
    @Mapping(target = "materiais", ignore = true)
    void updateEntity(EditoraRequestDTO dto, @MappingTarget Editora editora);

    EditoraResponseDTO toResponseDTO(Editora editora);
}
