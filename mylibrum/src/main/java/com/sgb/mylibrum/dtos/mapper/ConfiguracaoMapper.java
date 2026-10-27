package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.services.ConfiguracaoChaves;

@Mapper(config = MapeamentoConfig.class)
public interface ConfiguracaoMapper {

    default Configuracao toEntity(ConfiguracaoRequestDTO dto) {
        Configuracao configuracao = new Configuracao();
        updateEntity(dto, configuracao);
        return configuracao;
    }

    default void updateEntity(ConfiguracaoRequestDTO dto, Configuracao configuracao) {
        // Valor mascarado devolvido pela API não deve sobrescrever a credencial já armazenada.
        boolean manterValor = ConfiguracaoChaves.MASCARA.equals(dto.getValor())
                && configuracao.getValor() != null
                && ConfiguracaoChaves.ehSensivel(configuracao.getChave());
        copiarCampos(dto, configuracao);
        if (!manterValor) {
            configuracao.setValor(dto.getValor());
        }
    }

    @IgnorarCamposDeEntidade
    @Mapping(target = "valor", ignore = true)
    @Mapping(target = "filial", source = "filialId",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copiarCampos(ConfiguracaoRequestDTO dto, @MappingTarget Configuracao configuracao);

    @Mapping(target = "filialId", source = "filial.id")
    @Mapping(target = "valor", expression = "java(valorExibido(configuracao))")
    ConfiguracaoResponseDTO toResponseDTO(Configuracao configuracao);

    default String valorExibido(Configuracao configuracao) {
        return ConfiguracaoChaves.ehSensivel(configuracao.getChave())
                ? ConfiguracaoChaves.MASCARA
                : configuracao.getValor();
    }
}
