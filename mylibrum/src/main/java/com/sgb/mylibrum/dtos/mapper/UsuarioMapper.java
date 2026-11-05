package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.Usuario;

@Mapper(config = MapeamentoConfig.class)
public interface UsuarioMapper {

    default Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        updateEntity(dto, usuario);
        return usuario;
    }

    // A senha é codificada e definida pelo serviço; função e filial só mudam quando informadas.
    @IgnorarCamposDeEntidade
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "emprestimos", ignore = true)
    @Mapping(target = "funcao", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "filial", source = "filialId",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UsuarioRequestDTO dto, @MappingTarget Usuario usuario);

    @Mapping(target = "filialId", source = "filial.id")
    UsuarioResponseDTO toResponseDTO(Usuario usuario);
}
