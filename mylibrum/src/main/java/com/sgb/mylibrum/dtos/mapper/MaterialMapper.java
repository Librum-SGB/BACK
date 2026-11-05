package com.sgb.mylibrum.dtos.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.entities.Material;

@Mapper(config = MapeamentoConfig.class)
public interface MaterialMapper {

    default Material toEntity(MaterialRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Material material = new Material();
        updateEntity(dto, material);
        return material;
    }

    // Editora e autor principal são limpos quando o id não é informado; as coleções são tratadas abaixo.
    @IgnorarCamposDeEntidade
    @Mapping(target = "quantidadeExemplares", ignore = true)
    @Mapping(target = "autores", ignore = true)
    @Mapping(target = "generos", ignore = true)
    @Mapping(target = "editora", source = "editoraId")
    @Mapping(target = "autor", source = "autorId")
    void updateEntity(MaterialRequestDTO dto, @MappingTarget Material material);

    @AfterMapping
    default void atualizarColecoes(MaterialRequestDTO dto, @MappingTarget Material material) {
        material.getAutores().clear();
        if (dto.getAutorIds() != null) {
            dto.getAutorIds().forEach(id -> {
                Autor autor = new Autor();
                autor.setId(id);
                material.getAutores().add(autor);
            });
        }
        material.getGeneros().clear();
        if (dto.getGeneroIds() != null) {
            dto.getGeneroIds().forEach(id -> {
                Genero genero = new Genero();
                genero.setId(id);
                material.getGeneros().add(genero);
            });
        }
    }

    @Mapping(target = "autorId", source = "autor.id")
    @Mapping(target = "editoraId", source = "editora.id")
    @Mapping(target = "autorIds", source = "autores")
    @Mapping(target = "generoIds", source = "generos")
    MaterialResponseDTO toResponseDTO(Material material);
}
