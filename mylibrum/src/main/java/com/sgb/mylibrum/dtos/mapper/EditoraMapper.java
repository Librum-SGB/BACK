package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.entities.Editora;
import org.springframework.stereotype.Component;

@Component
public class EditoraMapper {

    public Editora toEntity(EditoraRequestDTO dto) {
        Editora editora = new Editora();
        updateEntity(dto, editora);
        return editora;
    }

    public void updateEntity(EditoraRequestDTO dto, Editora editora) {
        editora.setNome(dto.getNome());
        editora.setNacionalidade(dto.getNacionalidade());
    }

    public EditoraResponseDTO toResponseDTO(Editora editora) {
        EditoraResponseDTO dto = new EditoraResponseDTO();
        dto.setId(editora.getId());
        dto.setNome(editora.getNome());
        dto.setNacionalidade(editora.getNacionalidade());
        dto.setDataCriacao(editora.getDataCriacao());
        dto.setDataUltimaAtualizacao(editora.getDataUltimaAtualizacao());
        return dto;
    }
}
