package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sgb.mylibrum.dtos.request.HistoricoMultaRequestDTO;
import com.sgb.mylibrum.dtos.response.HistoricoMultaResponseDTO;
import com.sgb.mylibrum.entities.HistoricoMulta;

@Mapper(config = MapeamentoConfig.class)
public interface HistoricoMultaMapper {

    default HistoricoMulta toEntity(HistoricoMultaRequestDTO dto) {
        HistoricoMulta multa = new HistoricoMulta();
        updateEntity(dto, multa);
        return multa;
    }

    @IgnorarCamposDeEntidade
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataGeracao", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "emprestimo", source = "emprestimoId",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(HistoricoMultaRequestDTO dto, @MappingTarget HistoricoMulta multa);

    @Mapping(target = "emprestimoId", source = "emprestimo.id")
    @Mapping(target = "usuarioId", source = "usuario.id")
    HistoricoMultaResponseDTO toResponseDTO(HistoricoMulta multa);
}
