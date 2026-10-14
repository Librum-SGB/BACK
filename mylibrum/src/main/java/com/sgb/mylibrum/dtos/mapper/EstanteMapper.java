package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Filial;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class EstanteMapper {

    public Estante toEntity(EstanteRequestDTO dto) {
        Estante estante = new Estante();
        updateEntity(dto, estante);
        return estante;
    }

    public void updateEntity(EstanteRequestDTO dto, Estante estante) {
        BeanUtils.copyProperties(dto, estante,
                "id", "dataCriacao", "dataUltimaAtualizacao", "filialId");
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            estante.setFilial(filial);
        }
    }

    public EstanteResponseDTO toResponseDTO(Estante estante) {
        EstanteResponseDTO dto = new EstanteResponseDTO();
        BeanUtils.copyProperties(estante, dto);
        if (estante.getFilial() != null) {
            dto.setFilialId(estante.getFilial().getId());
        }
        return dto;
    }
}
