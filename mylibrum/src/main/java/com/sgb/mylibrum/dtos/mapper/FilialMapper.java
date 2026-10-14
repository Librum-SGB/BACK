package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class FilialMapper {

    public Filial toEntity(FilialRequestDTO dto) {
        Filial filial = new Filial();
        updateEntity(dto, filial);
        return filial;
    }

    public void updateEntity(FilialRequestDTO dto, Filial filial) {
        BeanUtils.copyProperties(dto, filial, "id", "dataCriacao", "dataUltimaAtualizacao");
    }

    public FilialResponseDTO toResponseDTO(Filial filial) {
        FilialResponseDTO dto = new FilialResponseDTO();
        BeanUtils.copyProperties(filial, dto);
        return dto;
    }
}
