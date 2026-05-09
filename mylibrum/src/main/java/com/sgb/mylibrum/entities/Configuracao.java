package com.sgb.mylibrum.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "configuracoes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"filial_id", "chave"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Configuracao extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filial_id", nullable = false)
    private Filial filial;

    @Column(nullable = false, length = 50)
    private String chave;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String valor;

    @Column(length = 255)
    private String descricao;
}