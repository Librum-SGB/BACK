package com.sgb.mylibrum.entities;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lista_tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ListaTarefa extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestor_id", nullable = false)
    private Gestor gestor;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar default 'MEDIA'")
    private PrioridadeTarefa prioridade = PrioridadeTarefa.MEDIA;

    @Column(columnDefinition = "boolean default false")
    private Boolean concluida = false;
}