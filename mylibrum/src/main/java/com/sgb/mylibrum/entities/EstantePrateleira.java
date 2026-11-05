package com.sgb.mylibrum.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estante_prateleiras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EstantePrateleira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estante_id", nullable = false)
    private Estante estante;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "position_min", nullable = false)
    private Integer posicaoInicial;

    @Column(name = "position_max", nullable = false)
    private Integer posicaoFinal;

    @Column(nullable = false)
    private Integer ordem;
}
