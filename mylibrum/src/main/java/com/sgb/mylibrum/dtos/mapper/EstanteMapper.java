package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.dtos.response.EstantePrateleiraResponseDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class EstanteMapper {

    public Estante toEntity(EstanteRequestDTO dto) {
        Estante estante = new Estante();
        updateEntity(dto, estante);
        return estante;
    }

    public void updateEntity(EstanteRequestDTO dto, Estante estante) {
        BeanUtils.copyProperties(dto, estante,
                "id", "dataCriacao", "dataUltimaAtualizacao", "prateleiras");
    }

    public EstanteResponseDTO toResponseDTO(Estante estante) {
        EstanteResponseDTO dto = new EstanteResponseDTO();
        BeanUtils.copyProperties(estante, dto);
        if (estante.getFilial() != null) {
            dto.setFilialId(estante.getFilial().getId());
        }
        dto.setPrateleiras(estante.getPrateleiras() == null ? new ArrayList<>()
                : estante.getPrateleiras().stream().map(prateleira -> {
                    EstantePrateleiraResponseDTO prateleiraDTO = new EstantePrateleiraResponseDTO();
                    prateleiraDTO.setId(prateleira.getId());
                    prateleiraDTO.setNome(prateleira.getNome());
                    prateleiraDTO.setPosicaoInicial(prateleira.getPosicaoInicial());
                    prateleiraDTO.setPosicaoFinal(prateleira.getPosicaoFinal());
                    prateleiraDTO.setOrdem(prateleira.getOrdem());
                    return prateleiraDTO;
                }).toList());
        return dto;
    }
}
