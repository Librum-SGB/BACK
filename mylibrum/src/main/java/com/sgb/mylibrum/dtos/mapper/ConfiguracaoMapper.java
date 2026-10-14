package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.entities.Filial;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoMapper {

    public Configuracao toEntity(ConfiguracaoRequestDTO dto) {
        Configuracao configuracao = new Configuracao();
        updateEntity(dto, configuracao);
        return configuracao;
    }

    public void updateEntity(ConfiguracaoRequestDTO dto, Configuracao configuracao) {
        BeanUtils.copyProperties(dto, configuracao,
                "id", "dataCriacao", "dataUltimaAtualizacao", "filialId");
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            configuracao.setFilial(filial);
        }
    }

    public ConfiguracaoResponseDTO toResponseDTO(Configuracao configuracao) {
        ConfiguracaoResponseDTO dto = new ConfiguracaoResponseDTO();
        BeanUtils.copyProperties(configuracao, dto);
        if (configuracao.getFilial() != null) {
            dto.setFilialId(configuracao.getFilial().getId());
        }
        return dto;
    }
}
