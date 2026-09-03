package com.sgb.mylibrum.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

import com.sgb.mylibrum.entities.enums.TipoMaterial;

@Entity
@Table(name = "materiais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
