package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.dtos.response.GeneroResponseDTO;
import com.sgb.mylibrum.entities.Genero;
import org.springframework.stereotype.Component;

@Component
public class GeneroMapper {

    public Genero toEntity(GeneroRequestDTO dto) {
        Genero genero = new Genero();

        genero.setNome(dto.getNome());
        genero.setDescricao(dto.getDescricao());

        return genero;
    }

    public GeneroResponseDTO toResponseDTO(Genero genero) {
        GeneroResponseDTO dto = new GeneroResponseDTO();

        dto.setId(genero.getId());
        dto.setNome(genero.getNome());
        dto.setDescricao(genero.getDescricao());
        dto.setDataCriacao(genero.getDataCriacao());
        dto.setDataUltimaAtualizacao(genero.getDataUltimaAtualizacao());

        return dto;
    }
    //não cria nem retorna um objeto novo. Ele modifica o objeto genero que já existe na memória.
    public void updateEntityFromDTO(GeneroRequestDTO dto, Genero genero) {
        genero.setNome(dto.getNome());
        genero.setDescricao(dto.getDescricao());
    }

}