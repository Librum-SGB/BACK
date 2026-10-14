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

    public MaterialResponseDTO toResponseDTO(Material material) {

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
        dto.setObservacao(material.getObservacao());
        dto.setIsbn(material.getIsbn());
        dto.setEdicao(material.getEdicao());
        dto.setAnoPublicacao(material.getAnoPublicacao());
        dto.setQuantidadePaginas(material.getQuantidadePaginas());
        dto.setQuantidadeExemplares(material.getQuantidadeExemplares());

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
        updateEntity(materialRequestDTO, material);
        return material;
    }

    public void updateEntity(MaterialRequestDTO dto, Material material) {
        material.setTitulo(dto.getTitulo());
        material.setTipo(dto.getTipo());
        material.setSubtitulo(dto.getSubtitulo());
        material.setSinopse(dto.getSinopse());
        material.setIssn(dto.getIssn());
        material.setTema(dto.getTema());
        material.setDescricao(dto.getDescricao());
        material.setObservacao(dto.getObservacao());
        material.setIsbn(dto.getIsbn());
        material.setEdicao(dto.getEdicao());
        material.setAnoPublicacao(dto.getAnoPublicacao());
        material.setQuantidadePaginas(dto.getQuantidadePaginas());

        if (dto.getEditoraId() != null) {
            Editora editora = new Editora();
            editora.setId(dto.getEditoraId());
            material.setEditora(editora);
        } else {
            material.setEditora(null);
        }

        if (dto.getAutorId() != null) {
            Autor autor = new Autor();
            autor.setId(dto.getAutorId());
            material.setAutor(autor);
        } else {
            material.setAutor(null);
        }

        if (dto.getAutorIds() != null) {
            material.getAutores().clear();
            material.getAutores().addAll(dto.getAutorIds().stream().map(id -> {
                Autor autor = new Autor();
                autor.setId(id);
                return autor;
            }).collect(Collectors.toSet()));
        } else {
            material.getAutores().clear();
        }

        if (dto.getGeneroIds() != null) {
            material.getGeneros().clear();
            material.getGeneros().addAll(dto.getGeneroIds().stream().map(id -> {
                Genero genero = new Genero();
                genero.setId(id);
                return genero;
            }).collect(Collectors.toSet()));
        } else {
            material.getGeneros().clear();
        }
    }
}