package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.entities.Material;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MaterialMapper {

    public MaterialResponseDTO toDto(Material material) {

        if (material == null) {
            return null;
        }

        MaterialResponseDTO dto = new MaterialResponseDTO();

        dto.setId(material.getId());
        dto.setTitulo(material.getTitulo());
        dto.setTipo(material.getTipo());
        dto.setSubtitulo(material.getSubtitulo());
        dto.setSinopse(material.getSinopse());
        dto.setIssn(material.getIssn());
        dto.setTema(material.getTema());
        dto.setDescricao(material.getDescricao());
        dto.setIsbn(material.getIsbn());
        dto.setEdicao(material.getEdicao());
        dto.setAnoPublicacao(material.getAnoPublicacao());
        dto.setQuantidadePaginas(material.getQuantidadePaginas());

        if (material.getAutor() != null) {
            dto.setAutorId(material.getAutor().getId());
        }

        if (material.getEditora() != null) {
            dto.setEditoraId(material.getEditora().getId());
        }

        if (material.getAutores() != null) {
            dto.setAutorIds(
                    material.getAutores()
                            .stream()
                            .map(Autor::getId)
                            .collect(Collectors.toSet())
            );
        }

        if (material.getGeneros() != null) {
            dto.setGeneroIds(
                    material.getGeneros()
                            .stream()
                            .map(Genero::getId)
                            .collect(Collectors.toSet())
            );
        }

        if (material instanceof com.sgb.mylibrum.entities.EntidadeAuditavel entidadeAuditavel) {
            dto.setDataCriacao(entidadeAuditavel.getDataCriacao());
            dto.setDataUltimaAtualizacao(entidadeAuditavel.getDataUltimaAtualizacao());
        }

        return dto;
    }

    public Material toEntity(MaterialRequestDTO materialRequestDTO) {

        if (materialRequestDTO == null) {
            return null;
        }

        Material material = new Material();

        material.setTitulo(materialRequestDTO.getTitulo());
        material.setTipo(materialRequestDTO.getTipo());
        material.setSubtitulo(materialRequestDTO.getSubtitulo());
        material.setSinopse(materialRequestDTO.getSinopse());
        material.setIssn(materialRequestDTO.getIssn());
        material.setTema(materialRequestDTO.getTema());
        material.setDescricao(materialRequestDTO.getDescricao());
        material.setIsbn(materialRequestDTO.getIsbn());
        material.setEdicao(materialRequestDTO.getEdicao());
        material.setAnoPublicacao(materialRequestDTO.getAnoPublicacao());
        material.setQuantidadePaginas(materialRequestDTO.getQuantidadePaginas());

        if (materialRequestDTO.getEditoraId() != null) {
            Editora editora = new Editora();
            editora.setId(materialRequestDTO.getEditoraId());
            material.setEditora(editora);
        }

        if (materialRequestDTO.getAutorId() != null) {
            Autor autor = new Autor();
            autor.setId(materialRequestDTO.getAutorId());
            material.setAutor(autor);
        }

        if (materialRequestDTO.getAutorIds() != null) {
            material.setAutores(
                    materialRequestDTO.getAutorIds()
                            .stream()
                            .map(id -> {
                                Autor autor = new Autor();
                                autor.setId(id);
                                return autor;
                            })
                            .collect(Collectors.toSet())
            );
        }

        if (materialRequestDTO.getGeneroIds() != null) {
            material.setGeneros(
                    materialRequestDTO.getGeneroIds()
                            .stream()
                            .map(id -> {
                                Genero genero = new Genero();
                                genero.setId(id);
                                return genero;
                            })
                            .collect(Collectors.toSet())
            );
        }

        return material;
    }
}