package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.response.ListaTarefaResponseDTO;
import com.sgb.mylibrum.entities.ListaTarefa;
import com.sgb.mylibrum.entities.Usuario;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ListaTarefaMapper {

    public ListaTarefa toEntity(ListaTarefaRequestDTO dto) {
        ListaTarefa tarefa = new ListaTarefa();
        updateEntity(dto, tarefa);
        return tarefa;
    }

    public void updateEntity(ListaTarefaRequestDTO dto, ListaTarefa tarefa) {
        BeanUtils.copyProperties(dto, tarefa,
                "id", "dataCriacao", "dataUltimaAtualizacao", "usuarioId");
        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            tarefa.setUsuario(usuario);
        }
    }

    public ListaTarefaResponseDTO toResponseDTO(ListaTarefa tarefa) {
        ListaTarefaResponseDTO dto = new ListaTarefaResponseDTO();
        BeanUtils.copyProperties(tarefa, dto);
        if (tarefa.getUsuario() != null) {
            dto.setUsuarioId(tarefa.getUsuario().getId());
        }
        return dto;
    }
}
