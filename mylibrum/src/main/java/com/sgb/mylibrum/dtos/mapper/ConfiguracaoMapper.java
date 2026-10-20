package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.services.ConfiguracaoChaves;
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
        // Valor mascarado devolvido pela API não deve sobrescrever a credencial já armazenada.
        boolean manterValor = ConfiguracaoChaves.MASCARA.equals(dto.getValor())
                && configuracao.getValor() != null
                && ConfiguracaoChaves.ehSensivel(configuracao.getChave());
        String[] ignorar = manterValor
                ? new String[] {"id", "dataCriacao", "dataUltimaAtualizacao", "filialId", "valor"}
                : new String[] {"id", "dataCriacao", "dataUltimaAtualizacao", "filialId"};
        BeanUtils.copyProperties(dto, configuracao, ignorar);
        if (dto.getFilialId() != null) {
            Filial filial = new Filial();
            filial.setId(dto.getFilialId());
            configuracao.setFilial(filial);
        }
    }

    public ConfiguracaoResponseDTO toResponseDTO(Configuracao configuracao) {
        ConfiguracaoResponseDTO dto = new ConfiguracaoResponseDTO();
        BeanUtils.copyProperties(configuracao, dto);
        if (ConfiguracaoChaves.ehSensivel(configuracao.getChave())) {
            dto.setValor(ConfiguracaoChaves.MASCARA);
        }
        if (configuracao.getFilial() != null) {
            dto.setFilialId(configuracao.getFilial().getId());
        }
        return dto;
    }
}
