package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;

@Mapper(config = MapeamentoConfig.class)
public interface EmprestimoMapper {

    @IgnorarCamposDeEntidade
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataSaida", ignore = true)
    @Mapping(target = "dataDevolucaoEfetivada", ignore = true)
    @Mapping(target = "renovacoesContagem", ignore = true)
    @Mapping(target = "usuario", source = "usuarioId")
    @Mapping(target = "exemplar", source = "exemplarId")
    @Mapping(target = "usuarioResponsavel", source = "usuarioResponsavelId")
    Emprestimo toEntity(EmprestimoRequestDTO dto);

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "exemplarId", source = "exemplar.id")
    @Mapping(target = "usuarioResponsavelId", source = "usuarioResponsavel.id")
    EmprestimoResponseDTO toResponseDTO(Emprestimo emprestimo);
}
