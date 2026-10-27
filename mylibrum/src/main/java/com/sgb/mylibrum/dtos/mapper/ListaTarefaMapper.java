package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.response.ListaTarefaResponseDTO;
import com.sgb.mylibrum.entities.ListaTarefa;

@Mapper(config = MapeamentoConfig.class)
public interface ListaTarefaMapper {

    default ListaTarefa toEntity(ListaTarefaRequestDTO dto) {
        ListaTarefa tarefa = new ListaTarefa();
        updateEntity(dto, tarefa);
        return tarefa;
    }

    @IgnorarCamposDeEntidade
    @Mapping(target = "usuario", source = "usuarioId",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ListaTarefaRequestDTO dto, @MappingTarget ListaTarefa tarefa);

    @Mapping(target = "usuarioId", source = "usuario.id")
    ListaTarefaResponseDTO toResponseDTO(ListaTarefa tarefa);
}
