package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Material;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ExemplarMapper {

    public Exemplar toEntity(ExemplarRequestDTO dto) {
        Exemplar exemplar = new Exemplar();
        updateEntity(dto, exemplar);
        return exemplar;
    }

    public void updateEntity(ExemplarRequestDTO dto, Exemplar exemplar) {
        BeanUtils.copyProperties(dto, exemplar,
                "id", "dataCriacao", "dataUltimaAtualizacao", "materialId", "filialId", "estanteId");
        if (dto.getMaterialId() != null) {
            Material material = new Material();
            material.setId(dto.getMaterialId());
            exemplar.setMaterial(material);
        }
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            exemplar.setFilial(filial);
        }
        if (dto.getEstanteId() != null) {
            Estante estante = new Estante();
            estante.setId(dto.getEstanteId());
            exemplar.setEstante(estante);
        }
    }

    public ExemplarResponseDTO toResponseDTO(Exemplar exemplar) {
        ExemplarResponseDTO dto = new ExemplarResponseDTO();
        BeanUtils.copyProperties(exemplar, dto);
        if (exemplar.getMaterial() != null) {
            dto.setMaterialId(exemplar.getMaterial().getId());
        }
        if (exemplar.getFilial() != null) {
            dto.setFilialId(exemplar.getFilial().getId());
        }
        if (exemplar.getEstante() != null) {
            dto.setEstanteId(exemplar.getEstante().getId());
        }
        return dto;
    }
}
