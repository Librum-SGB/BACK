package com.sgb.mylibrum.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "livros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Livro extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

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
        name = "livros_autores",
        joinColumns = @JoinColumn(name = "livro_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();

    // Relacionamento N:M com Generos
    @ManyToMany
    @JoinTable(
        name = "livros_generos",
        joinColumns = @JoinColumn(name = "livro_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private Set<Genero> generos = new HashSet<>();
}