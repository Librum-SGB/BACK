package com.sgb.mylibrum.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "editoras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Editora extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(length = 100)
    private String nacionalidade;
}