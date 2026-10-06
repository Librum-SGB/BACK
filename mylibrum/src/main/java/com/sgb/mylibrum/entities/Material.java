package com.sgb.mylibrum.entities;

import java.util.HashSet;
import java.util.Set;

import com.sgb.mylibrum.entities.enums.TipoMaterial;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "materiais")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_material", discriminatorType = DiscriminatorType.STRING)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Material extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoMaterial tipo = TipoMaterial.LIVRO;

    private String subtitulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(length = 20)
    private String issn;

    private String tema;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(unique = true, length = 13)
    private String isbn;

    @Column(columnDefinition = "integer default 1")
    private Integer edicao = 1;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Column(name = "quantidade_paginas")
    private Integer quantidadePaginas;

    @Column(name = "quantidade_exemplares", nullable = false, columnDefinition = "integer default 0")
    private Integer quantidadeExemplares = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editora_id", nullable = false)
    private Editora editora;

    // Relacionamento N:M com Autores
    @ManyToMany
    @JoinTable(
        name = "materiais_autores",
        joinColumns = @JoinColumn(name = "material_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();

    // Relacionamento N:M com Generos
    @ManyToMany
    @JoinTable(
        name = "materiais_generos",
        joinColumns = @JoinColumn(name = "material_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private Set<Genero> generos = new HashSet<>();
}
